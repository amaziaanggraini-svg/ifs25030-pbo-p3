# ifs25030-java-oop

Lima studi kasus Java (Clean Architecture + Generics + Checked Exceptions):
`catatan-keuangan`, `buku-tamu`, `inventaris-barang`, `jadwal-kegiatan`, `kontak-teman`.

Struktur tiap proyek:

```
<proyek>/
├── src/          kode aplikasi (domain, usecase, adapter, framework, App.java)
├── test/         unit test (TestRunner) + penjalan test case (TestCaseRunner)
└── test-cases/   TC-01..TC-04 (.tc = input, .expected = cuplikan output dari soal)
```

## Menjalankan aplikasi (dari folder proyek)

```
javac -d output -sourcepath src src/App.java
java -cp output App
```

## Menjalankan test (dari folder proyek, tanpa library tambahan)

```
javac -d output-test -sourcepath src test/TestRunner.java test/TestCaseRunner.java

# unit test: repository, use case, InputUtil
java -cp output-test TestRunner

# test case soal (butuh aplikasi sudah di-compile ke folder "output")
java -cp output-test TestCaseRunner output
```
