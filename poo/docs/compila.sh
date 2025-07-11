#!/bin/bash

# Crea la cartella per i file temporanei
mkdir -p build

# Compilazione (due volte per sicurezza)
pdflatex -output-directory=build Documentazione-Programmazione-Object-Oriented-OOBD39.tex
pdflatex -output-directory=build Documentazione-Programmazione-Object-Oriented-OOBD39.tex

# Copia solo il PDF accanto al .tex
mv build/Documentazione-Programmazione-Object-Oriented-OOBD39.pdf .
