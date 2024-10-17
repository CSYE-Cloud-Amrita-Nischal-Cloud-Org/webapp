#!/bin/bash

# Find all .sh files in the current directory and subdirectories
# and make them executable
find . -type f -name "*.sh" -exec chmod +x {} \;

echo "All .sh files have been made executable."