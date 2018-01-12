# tm

Time Series Merge Problem

Time series are stored in files with the following format:
    ● files are multiline plain text files in ASCII encoding
    ● each line contains exactly one record
    ● each record contains date and integer value; 
      records are encoded like so:  YYYY-MM-DD:X
    ● dates within single file are duplicate and sorted in ascending order
    ● files can be bigger than RAM available on target host

Program accepting arbitrary number of input file names as arguments, 
which merges two input files into one output file. 
Result file follow same format conventions as described above. 
Records with same date value will be merged into one by summing up X values.

## Installation

* java 8
* make uberjar

## Usage

    $ java -jar target/uberjar/tm.jar --help


Copyright © 2018

Distributed under the Eclipse Public License either version 1.0
