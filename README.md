# Jadoop
Barebones implementation of Distributed object storage using Java.


# Test
```
git clone https://github.com/abhishek-verma/Jadoop.git
cd jadoop/executable

# Start Master Server
java -jar JadoopMaster.jar

# Start Storage server, Execute on multiple systems to create multiple storage nodes
java -jar JadoopStorageServer.jar

# Client side commands
java -jar Jadoop.jar put files/a.txt a
java -jar Jadoop.jar get a files/b.txt
```

