#!/bin/sh

if [ ! -f theme_generator ]; then
    clang++ -std=c++11 theme_generator.cpp -o theme_generator
fi

./theme_generator
