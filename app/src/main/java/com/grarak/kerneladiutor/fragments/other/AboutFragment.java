/*
 * Copyright (C) 2015-2016 Willi Ye <williye97@gmail.com>
 *
 * This file is part of Kernel Adiutor.
 *
 * Kernel Adiutor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kernel Adiutor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kernel Adiutor.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.grarak.kerneladiutor.fragments.other;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.BaseFragment;
import com.grarak.kerneladiutor.fragments.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.entity.Library;

import java.util.List;

/**
 * Created by willi on 22.07.16.
 */
public class AboutFragment extends RecyclerViewFragment {

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(new InfoFragment());
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {
        librariesInit(items);
    }

    private void librariesInit(List<RecyclerViewItem> items) {
        CardView cardView = new CardView(getActivity());
        cardView.setTitle(getString(R.string.libraries_used));

        Libs libs = new Libs(getActivity());
        libs.prepareLibraries(getActivity(), null, null, true, true);

        for (final Library lib : libs.getAutoDetectedLibraries(getActivity())) {
            DescriptionView descriptionView = new DescriptionView();
            descriptionView.setTitle(Utils.htmlFrom(lib.getLibraryName()) + " - " + lib.getAuthor());
            descriptionView.setSummary(Utils.htmlFrom(lib.getLibraryDescription()));
            descriptionView.setOnItemClickListener(new RecyclerViewItem.OnItemClickListener() {
                @Override
                public void onClick(RecyclerViewItem item) {
                    String link = null;
                    if (lib.getLibraryWebsite() != null) {
                        link = lib.getLibraryWebsite();
                    } else if (lib.getRepositoryLink() != null) {
                        link = lib.getRepositoryLink();
                    } else if (lib.getAuthorWebsite() != null) {
                        link = lib.getAuthorWebsite();
                    }

                    if (link != null && !link.isEmpty()) {
                        Utils.launchUrl(link, getActivity());
                    }
                }
            });

            cardView.addItem(descriptionView);
        }
        items.add(cardView);
    }

    public static class InfoFragment extends BaseFragment {

        @Override
        protected boolean retainInstance() {
            return false;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_info, container, false);
            rootView.findViewById(R.id.image).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.launchUrl("https://github.com/Grarak", getActivity());
                }
            });
            return rootView;
        }
    }

}
