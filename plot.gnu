#!/usr/bin/env gnuplot
# set xrange [0:1000]
# set yrange [1000:4000]
set terminal png size 800,600
set output 'plot.png'
plot "run.tsv" using 1:2 title "Max" with lines, \
     "run.tsv" using 1:3 title "Avg" with lines
# pause 1
# reread 
