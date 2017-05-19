# TabbyXL
A research system for rule-based transformation spreadsheet data from arbitrary to relational tables

## Build with Apache Maven

In order to build the executable JAR file run the following command:

```bash
mvn clean compile assembly:single
```

Then you can run it to ensure the build process worked.

```bash
java -jar target/tabbyxl.jar -input samples/sampl.xlsx -ruleset samples/sampl.dslr
```

If the program outputs (at the end) something like
```
Total number of
	tables: 4
	cells: 105
	not empty cells: 99
	labels: 50
	entries: 51
	label-label pairs: 0
	entry-label pairs: 219
	category-label pairs: 50
	categories: 17
	label groups: 17

Total rule firing time: 91
```
then it works.

## Makefile

There is a Makefile that can be used to build project's artifacts (now it is just the stand-alone executable JAR).

```bash
make build    # build executable JAR
make run      # Run the script
```

## Makefile targets

 * `assembly-single`, `single`, `jar` are synonyms and denote building and construction of executable JAR file;
 * `install`, `all` is used to build and install the package (e.g. in ~/.mv2/...) to be used in other projects;
 * `clean` is obviously used to clean the source tree, removing [hopefully] everything generated by building process;
 * `compile` used to download and compile dependencies;
 * `run` runs testing script (It is not a unit test).

## Usage
```bash
java -jar tabbyxl.jar <params>

Params:
-input <input excel file>          an input excel workbook (*.xlsx file)
-sheets <sheet indexes>            sheet indexes in the input excel workbook (e.g. "0-2,4,5,7-10")
-ruleset <drl or dslr file>        a ruleset (*.drl or *.dslr file)
-categorySpec <category directory> a directory with category specifications in YAML (*.cat files)
-output <output directory>         a directory for outputting results
-ignoreSuperscript <true|false>    specify true to ignore superscript text in cells (false used by default)
-useCellText <true|false>          specify true to use cell values as text (false used by default)
-useShortNames <true|false>        specify true to use short names (just sheet names) for output files (false used by default)
-debuggingMode <true|false>        specify true to turn on debugging mode (false used by default)
-help                              print the usage
```
## License

Apache-2.0.