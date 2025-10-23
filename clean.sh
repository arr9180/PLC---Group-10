#!/bin/bash
# Delete all compiled class files inside provided and testers packages

find provided -name "*.class" -type f -delete
find testers -name "*.class" -type f -delete

echo "All .class files in provided and testers folders have been deleted."
