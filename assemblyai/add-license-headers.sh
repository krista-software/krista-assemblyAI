#!/bin/bash

# Script to add GPLv3 license headers to all Java files
# Usage: ./add-license-headers.sh

LICENSE_HEADER="/*
 * AssemblyAI Extension for Krista
 * Copyright (C) 2024 Krista Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
"

# Find all Java files
find src -name "*.java" -type f | while read -r file; do
    # Check if file already has license header
    if ! grep -q "GNU General Public License" "$file"; then
        echo "Adding license header to: $file"
        
        # Create temporary file with license header
        echo "$LICENSE_HEADER" > "$file.tmp"
        
        # Append original file content
        cat "$file" >> "$file.tmp"
        
        # Replace original file
        mv "$file.tmp" "$file"
    else
        echo "License header already exists in: $file"
    fi
done

echo "License headers added successfully!"

