cd build-test-data 
yes | grails $1 
cd ../testOptionalJars 
yes | grails $1 
cd ../bookStore 
yes | grails $1 
cd ../baseTests
yes | grails $1 
cd ..
