#!/bin/bash

a2ps --toc *.java massim/*.java agents/*.java -o massim_big.ps -t The massim package  -T 4 -1
ps2pdf massim_big.ps massim_big_`date  +"%Y%m%d-%H%M"`.pdf
rm massim_big.ps

a2ps --toc *.java experiments/ex1/*.java -o exp_big.ps -t The experiments package  -T 4 -1
ps2pdf exp_big.ps exp_big_`date  +"%Y%m%d-%H%M"`.pdf
rm exp_big.ps

a2ps --toc tests/comm/*.java -o tests_big.ps -t The tests package  -T 4 -1
ps2pdf tests_big.ps tests_big_`date  +"%Y%m%d-%H%M"`.pdf
rm tests_big.ps

mv *.pdf ../doc/
