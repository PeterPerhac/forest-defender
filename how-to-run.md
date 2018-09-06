no time to fully fix the ant build shit:
  delete all inside build/applet, if exists
  install ant, if required


```bash
  brew install ant
  ant compile
  mkdir -p build/applet/temp/jar
  ant jar
  ant build-and-run
```


