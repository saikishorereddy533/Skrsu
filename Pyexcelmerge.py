#!/bin/bash

# Define the input files and user IDs to search
input_files=("file1.xlsx" "file2.xlsx" "file3.xlsx") # Add your files here
user_ids=("user1" "user2" "user3" "user4" "user5")   # Define your user IDs here

# Join the array of user IDs with commas to pass them as an argument to Python
user_ids_str=$(IFS=,; echo "${user_ids[*]}")

# Run the Python script to search the Excel files
python3 << END
import pandas as pd

# User IDs to search for
user_ids = "$user_ids_str".split(",")

# Input files
input_files = ["file1.xlsx", "file2.xlsx", "file3.xlsx"]  # Same input files here

# DataFrame to hold all matched rows
matched_records = pd.DataFrame()

# Search for records in each file
for file in input_files:
    xls = pd.ExcelFile(file)  # Read the Excel file
    for sheet_name in xls.sheet_names:
        df = pd.read_excel(xls, sheet_name=sheet_name)  # Read each sheet
        matched = df[df.iloc[:, 0].isin(user_ids)]      # Filter rows where the first column matches user IDs
        if not matched.empty:
            matched['SourceFile'] = file  # Add a column to know which file the data came from
            matched['SheetName'] = sheet_name  # Add a column to know which sheet the data came from
            matched_records = pd.concat([matched_records, matched])  # Append the matched rows

# Output the filtered data to a new Excel file
if not matched_records.empty:
    matched_records.to_excel("filtered_records.xlsx", index=False)
    print("Filtered records saved to 'filtered_records.xlsx'")
else:
    print("No matching records found.")
END
