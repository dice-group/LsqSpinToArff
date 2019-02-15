# LsqSpinToArff

This is a file format converter.

A usual workflow is listed in the following:

- Create a positive set and a negative set of SPARQL queries using SPAB.
- Use LSQ to extract SPARQL features of the single queries. The resulting files are in TURTLE format, and are using SPIN as well as LSQ vocabulary.
- Use this project to create an integrated ARFF file.
- Use Weka for data analysis.

Some documentation is available in the wiki at https://github.com/dice-group/LsqSpinToArff/wiki