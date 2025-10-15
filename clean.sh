#!/bin/bash
# Delete all .class files in phase2 folder

find phase2 -name "*.class" -type f -delete

echo "All .class files in phase2 folder have been deleted."
