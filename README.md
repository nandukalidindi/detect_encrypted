Encrypted Folder -- Code

 Jars Folder -- Jars used

Main Class : DetectEncrypted in Encrypted/src/DetectEncrypted.java

sample-run:


java -jar Detect_Encrypted.jar /Users/username/Downloads ALL

>Traverses from /Users/username/Downloads folder searching for all encrypted files recursively.

>ALL is the fileType parameter which checks for all fileTypes i.e Document, SpreadSheet, Presentation and PDF. FileType parameter is case ignored.
java -jar Detect_Encrypted.jar /Users/username/Downloads pdf

>Checks for only pdf documents

**A CSV File with the list of all the encrypted files is created at the location where the jar is executed.

