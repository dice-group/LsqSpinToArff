# LsqSpinToArff

This is a file format converter.

A usual workflow is listed in the following:

- Create a positive set and a negative set of SPARQL queries using [SPAB](https://github.com/dice-group/SPAB).
- Use [LSQ](https://dice-group.github.io/LSQ/) to extract SPARQL features of the single queries. The resulting files are in TURTLE format, and are using SPIN as well as LSQ vocabulary.
- Use this project to create an integrated ARFF file.  
  [Example code](https://github.com/dice-group/LsqSpinToArff/blob/master/src/test/java/org/dice_research/LsqSpinToArff/LsqSpinToArffTest.java) and [example files](https://github.com/dice-group/LsqSpinToArff/tree/master/src/main/resources) are available.
- Use [Weka](https://www.cs.waikato.ac.nz/ml/weka/) for data analysis.

Some documentation is available in the wiki at https://github.com/dice-group/LsqSpinToArff/wiki

# Installation

## LSQ

- Download [LSQ sources at GitHub](https://github.com/dice-group/LSQ/releases/tag/v1.0.0)
- Run `mvn clean install`