# Jadoop
Barebones implementation of Distributed object storage using Java.


# Test
```
git clone https://github.com/abhishek-verma/Jadoop.git
cd jadoop/out/artifacts/executable
java -jar JadoopMaster.jar
java -jar JadoopStorageServer.jar # Execute on multiple systems to create multiple nodes

java -jar Jadoop.jar put files/a.txt a
java -jar Jadoop.jar get a files/b.txt
```

