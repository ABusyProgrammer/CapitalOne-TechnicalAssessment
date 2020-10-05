# Capital One Technical Assessment: Automated Code Checker
The purpose of this project is to build an automated checker whenever code is merged into a build pipeline.

## Approach
The goal was to generify the program as much as possible, in order for it to work on multiple different programming languages. The following functionality is provided:

The program can parse through Python, Java, JavaScript, C++, Kotlin, Scala and Groovy files.

## Assumptions
The following assumptions were made:

1. For parsing Python programs, docstrings were not counted as comments. The assumption made here was that docstrings do not count as comments given that they are accessible by the program at run-time. Also, the official documentation for Python states that a block comment is a set of lines beginning with a # character, and not the triple-quotes used to generate a docstring.
2. Comment characters that occur within a string are not parsed as comments.
3. The program can check multiple code files. All files can be passed in as command-line arguments via their relative or absolute paths.

## How to Run
To run, simply execute the program using `java CalculateComments.java <FILE_PATH> ...`. Multiple files can be specified as command-line arguments, and the path can be relative or absolute.

## Author
Aryan Kukreja (aryan.s.kukreja@gmail.com)

Test code and problem statement provided by Capital One
