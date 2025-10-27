# Phase 03 Folder Setup
This file contains **provided code**, **testers**, and **test cases**.

- `provided` should be a top level package in your src directory
- JottParserTester and JottTokenizerTester classes should be in a package called `testers`.
- The testCases directories should be in the working directory of your project. 


# How to Run Phase 03
1. Compile everything:    
`javac provided/**/*.java testers/**/*.java`
2. Run tokenizer tester:  
`java testers.JottTokenizerTester`
3. Run parser tester:     
`java testers.JottParserTester`
4. Run main tester:       
`java testers.JottMainTester`
5. Remove .class files:   
`./clean.sh`