#!/bin/bash

# Crea la cartella per i file temporanei
mkdir -p build

# Compilazione (due volte per sicurezza)
pdflatex -output-directory=build Documentazione-BaseDiDati-OOBD39.tex
pdflatex -output-directory=build Documentazione-BaseDiDati-OOBD39.tex

# Copia solo il PDF accanto al .tex
mv build/Documentazione-BaseDiDati-OOBD39.pdf .

# Sposta tutti i file generati (tranne .tex e .pdf) nella cartella principale dentro build
for f in Documentazione-BaseDiDati-OOBD39.*; do
  case "$f" in
    *.tex|*.pdf)
      # non spostare
      ;;
    *)
      if [ -f "$f" ]; then
        mv "$f" build/
      fi
      ;;
  esac
done