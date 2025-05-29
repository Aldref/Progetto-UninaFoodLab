#!/bin/bash

# Crea la cartella per i file temporanei
mkdir -p build

# Compilazione (due volte per sicurezza)
pdflatex -output-directory=build Documentazione_Programmazione-Object-Oriented.tex
pdflatex -output-directory=build Documentazione_Programmazione-Object-Oriented.tex

# Copia solo il PDF accanto al .tex
mv build/Documentazione_Programmazione-Object-Oriented.pdf .
