# animagic

### Maven Install
```mvn clean source:jar install```

### Deploy to central repository
This will require having credentials at oss.sonatype.org for the com.bytebreakstudios group and setting up a settings.xml file in your local .m2 directory.

```mvn deploy```

If the version has a -SNAPSHOT then it will go to the snapshot repository.  If it doesn't, it will go to the staging repository.  You will have to go to oss.sonatype and manually release from the UI there.