#include <fstream>
#include <iostream>
#include <sys/stat.h>
#include <vector>

class Color {
public:
    Color() = delete;

    explicit Color(std::string name, std::string color, std::string darkColor,
                   std::string accentColor)
            : name(std::move(name)),
              color(std::move(color)),
              darkColor(std::move(darkColor)),
              accentColor(std::move(accentColor)) {
        primaryName = this->name;
        primaryName.append("Primary");

        primaryDarkName = this->name;
        primaryDarkName.append("PrimaryDark");

        accentName = this->name;
        accentName.append("Accent");
    }

    std::string name;
    std::string color;
    std::string darkColor;
    std::string accentColor;

    std::string primaryName;
    std::string primaryDarkName;
    std::string accentName;
};

class ColorPair {
public:
    ColorPair() = delete;

    explicit ColorPair(Color primary, Color accent)
            : primary(std::move(primary)),
              accent(std::move(accent)) {}

    Color primary;
    Color accent;
};

class Theme {
public:
    Theme() = delete;

    explicit Theme(ColorPair pair, bool dark);

    std::string name;

    std::string primaryColorName;
    std::string primaryDarkColorName;
    std::string accentColorName;

    std::string primaryColorRes;
    std::string primaryDarkColorRes;
    std::string accentColorRes;

    std::string parentTheme;

    std::string editTextBuildPropThemeRes;

    bool dark;

    bool isDark() {
        return dark;
    }

private:
    void formatColorName(std::string *name);
};

Theme::Theme(ColorPair pair, bool dark) {
    name.append("Theme.");
    name.append(pair.primary.name);
    name.append(".");
    name.append(pair.accent.name);
    if (dark) {
        name.append(".Dark");
    }
    formatColorName(&name);

    primaryColorName = pair.primary.primaryName;
    primaryDarkColorName = pair.primary.primaryDarkName;
    accentColorName = pair.accent.accentName;

    primaryColorRes = "@color/";
    primaryColorRes.append(primaryColorName);

    primaryDarkColorRes = "@color/";
    primaryDarkColorRes.append(primaryDarkColorName);

    accentColorRes = "@color/";
    accentColorRes.append(accentColorName);

    parentTheme = dark ? "AppThemeParentDark" : "AppThemeParent";

    editTextBuildPropThemeRes = "@style/";
    editTextBuildPropThemeRes.append(name);
    if (!dark) {
        editTextBuildPropThemeRes.append(".Dark");
    }

    this->dark = dark;
}

void Theme::formatColorName(std::string *name) {
    (*name)[0] = (char) toupper((*name)[0]);
    for (int i = 0; i < name->size(); i++) {
        if ((*name)[i] == '_' || (*name)[i] == '.') {
            if ((*name)[i + 1] != '\0') {
                (*name)[i + 1] = (char) toupper((*name)[i + 1]);
            }
        }
    }
}

const Color colors[] = { // NOLINT
        Color("default", "#2A7289", "#00475C", "#5EA1b9"),
        Color("red", "#F44336", "#D32F2F", "#FF5252"),
        Color("pink", "#E91E63", "#E91E63", "#FF80AA"),
        Color("purple", "#9C27B0", "#7B1FA2", "#E040FB"),
        Color("blue", "#2196F3", "#1976D2", "#448AFF"),
        Color("green", "#4CAF50", "#388E3C", "#69F0AE"),
        Color("orange", "#FF9800", "#F57C00", "#FFAB40"),
        Color("brown", "#795548", "#5D4037", "#A98274"),
        Color("grey", "#9E9E9E", "#616161", "#CFCFCF"),
        Color("blue_grey", "#607D8B", "#455A64", "#8EACBB"),
        Color("teal", "#009688", "#00796B", "#64FFDA"),
        Color("deep_purple", "#673AB7", "#512DA8", "#7C4DFF"),
        Color("lime", "#CDDC39", "#AFB42B", "#EEFF41"),
        Color("indigo", "#3F51B5", "#303F9F", "#536DFE"),
        Color("cyan", "#00BCD4", "#0097A7", "#18FFFF"),
        Color("deep_orange", "#FF5722", "#E64A19", "#FF6E40"),
};

std::string getStyleItem(const std::string &name, const std::string &value) {
    std::string ret = "<item name=\"";
    ret.append(name);
    ret.append("\">");
    ret.append(value);
    ret.append("</item>");
    return ret;
}

std::string getStyleColor(const std::string &name, const std::string &value) {
    std::string ret = "<color name=\"";
    ret.append(name);
    ret.append("\">");
    ret.append(value);
    ret.append("</color>");
    return ret;
}

void createColorPairs(std::vector<ColorPair> *colorPairs) {
    for (const auto &primaryColor : colors) {
        for (const auto &accentColor : colors) {
            if (primaryColor.name != accentColor.name) {
                colorPairs->push_back(ColorPair(primaryColor, accentColor));
            }
        }
    }
}

void createThemes(const std::vector<ColorPair> &colorPairs, std::vector<Theme> *themes) {
    for (const auto &colorPair : colorPairs) {
        Theme theme(colorPair, false);
        Theme themeDark(colorPair, true);

        themes->push_back(theme);
        themes->push_back(themeDark);
    }
}

int main(int argc, char **argv) {

    struct stat st = {0};

    if (stat("themes", &st) == -1) {
        mkdir("themes", 0777);
    }

    std::vector<ColorPair> colorPairs;
    createColorPairs(&colorPairs);

    std::vector<Theme> themes;
    createThemes(colorPairs, &themes);

    std::string themeStyles = R"(<?xml version="1.0" encoding="utf-8"?>)";
    themeStyles.append("\n<resources>\n\n");

    std::string javaArray;
    std::string javaArrayDark;

    for (auto &theme : themes) {
        themeStyles.append("    <style name=\"");

        themeStyles.append(theme.name);
        themeStyles.append("\" parent=\"");
        themeStyles.append(theme.parentTheme);
        themeStyles.append("\">\n");

        themeStyles.append("        ");
        themeStyles.append(getStyleItem("colorPrimary", theme.primaryColorRes)).append("\n");
        themeStyles.append("        ");
        themeStyles.append(getStyleItem("colorPrimaryDark", theme.primaryDarkColorRes)).append("\n");
        themeStyles.append("        ");
        themeStyles.append(getStyleItem("colorAccent", theme.accentColorRes)).append("\n");
        themeStyles.append("        ");
        themeStyles.append(getStyleItem("edittext_build_prop_style", theme.editTextBuildPropThemeRes)).append("\n");

        themeStyles.append("    </style>\n\n");

        std::string javaThemeKey = "\"";
        javaThemeKey.append(theme.primaryColorName).append(";");
        javaThemeKey.append(theme.accentColorName).append("\", ");

        std::string javaTheme = "new Theme(\"";
        javaTheme.append(theme.primaryColorName).append("\", \"");
        javaTheme.append(theme.accentColorName).append("\", ");
        std::string themeName = theme.name;
        std::replace(themeName.begin(), themeName.end(), '.', '_');
        javaTheme.append("R.style.").append(themeName).append("));");
        if (theme.isDark()) {
            javaArrayDark.append("sThemesDark.put(").append(javaThemeKey).append(javaTheme).append("\n");
        } else {
            javaArray.append("sThemes.put(").append(javaThemeKey).append(javaTheme).append("\n");
        }
    }

    themeStyles.append("</resources>\n");

    std::ofstream stylesFile;
    stylesFile.open("themes/theme_styles.xml");
    stylesFile << themeStyles;
    stylesFile.flush();
    stylesFile.close();

    std::string javaPrimaryColors;
    std::string javaAccentColors;

    std::string themeColors = R"(<?xml version="1.0" encoding="utf-8"?>)";
    themeColors.append("\n<resources>\n");

    for (auto &color : colors) {
        javaPrimaryColors.append("sPrimaryColors.add(\"");
        javaPrimaryColors.append(color.primaryName);
        javaPrimaryColors.append("\");\n");

        javaAccentColors.append("sAccentColors.add(\"");
        javaAccentColors.append(color.accentName);
        javaAccentColors.append("\");\n");

        themeColors.append("    ");
        themeColors.append(getStyleColor(color.primaryName, color.color)).append("\n");
        themeColors.append("    ");
        themeColors.append(getStyleColor(color.primaryDarkName, color.darkColor)).append("\n");
        themeColors.append("    ");
        themeColors.append(getStyleColor(color.accentName, color.accentColor)).append("\n");
    }

    themeColors.append("</resources>\n");

    std::ofstream colorsFile;
    colorsFile.open("themes/theme_colors.xml");
    colorsFile << themeColors;
    colorsFile.flush();
    colorsFile.close();

    std::cout << javaPrimaryColors << std::endl;
    std::cout << javaAccentColors << std::endl;

    std::cout << javaArray << std::endl;
    std::cout << javaArrayDark;

    return 0;
}
