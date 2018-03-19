# Smart xml analyzer

The program to analize HTML files

## Getting Started
 - clone project 
```
git clone https://sukhomud@bitbucket.org/sukhomud/smart-xml-analyzer.git
```
- install maven 

- run to build executable jar file 
```
mvn clean compile assembly:single
```
- run jar 

```
java jar 
```
---

## Options

- ip - input origin file path
- op - input other sample file path
- id - element id

## Usage Example

```
java jar <your_bundled_app>.jar -ip "input_origin_file_path" -op "input_other_sample_file_path" -id "element id"
```