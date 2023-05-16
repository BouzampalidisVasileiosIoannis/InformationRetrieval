with open("G:\\8th_semester\\Information_Retrieval\\InformationRetrieval\\test.txt", "r") as file:
    # Read the contents of the file into a string variable
    file_contents = file.read()

    # Remove all tabs from the file contents
    no_tabs = file_contents.replace("  \n", "")

    # Print the resulting string with no tabs
    print(no_tabs)