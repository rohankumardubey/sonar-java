package checks.tests;

class JUnitMethodDeclarationCheck_JUnit4 {
  @org.junit.Test void test() { }

  public void setUp() { } // Noncompliant {{Annotate this method with JUnit4 '@org.junit.Before' or remove it.}}
  public void tearDown() { }  // Noncompliant {{Annotate this method with JUnit4 '@org.junit.After' or remove it.}}

  public static junit.framework.Test suite() { return null; }  // Noncompliant {{Remove this method, JUnit4 test suites are not relying on it anymore.}}
}

class UnitMethodDeclarationCheck_JUnit4_other_suite {
  @org.junit.Test void test() {}
  Integer suite() { return null; } // Compliant
}

class JUnitMethodDeclarationCheck_JUnit4_compliant {
  protected Object step() { return null; } // unrelated
  protected Object teaDown() { return null; } // typo from tearDown, but could be unrelated
  @org.junit.Test void test() { }
  @org.junit.Before public void setUp() { }
  @org.junit.After public void tearDown() { }
}

class JUnitMethodDeclarationCheck_JUnit4_compliant2 {
  @org.junit.Test void test() { }
  @org.junit.BeforeClass public void setUp() { }
  @org.junit.AfterClass public void tearDown() { }
}

abstract class AbstractJUnitMethodDeclarationCheck_JUnit4 {
  @org.junit.Before public void setUp() { }
}

class JUnitMethodDeclarationCheck_JUnit4_compliant3 extends AbstractJUnitMethodDeclarationCheck_JUnit4 {
  @org.junit.Test void test() { }

  @Override
  public void setUp() { } // Compliant
}

class JUnitMethodDeclarationCheck_JUnit5 {
  @org.junit.jupiter.api.Test void test() { }

  public void setUp() { } // Noncompliant {{Annotate this method with JUnit5 '@org.junit.jupiter.api.BeforeEach' or remove it.}}
  public void tearDown() { }  // Noncompliant {{Annotate this method with JUnit5 '@org.junit.jupiter.api.AfterEach' or remove it.}}

  public static junit.framework.Test suite() { return null; }  // Noncompliant {{Remove this method, JUnit5 test suites are not relying on it anymore.}}
}

class JUnitMethodDeclarationCheck_JUnit5_compliant {
  @org.junit.jupiter.api.Test void test() { }
  @org.junit.jupiter.api.BeforeEach public void setUp() { }
  @org.junit.jupiter.api.AfterEach public void tearDown() { }
}

class JUnitMethodDeclarationCheck_JUnit5_compliant2 {
  @org.junit.jupiter.api.Test void test() { }
  @org.junit.jupiter.api.BeforeAll public void setUp() { }
  @org.junit.jupiter.api.AfterAll public void tearDown() { }
}

class JUnitMethodDeclarationCheck_JUnit4_5_mixed {
  @org.junit.Test void junit4() { }
  @org.junit.jupiter.api.Test void junit5() { }

  // use JUnit 4 annotations
  @org.junit.Before public void setUp() { } // Noncompliant {{Annotate this method with JUnit5 '@org.junit.jupiter.api.BeforeEach' instead of JUnit4 '@Before'.}}
  @org.junit.After public void tearDown() { } // Noncompliant {{Annotate this method with JUnit5 '@org.junit.jupiter.api.AfterEach' instead of JUnit4 '@After'.}}

  @org.junit.Before public void before() { } // Noncompliant {{Annotate this method with JUnit5 '@org.junit.jupiter.api.BeforeEach' instead of JUnit4 '@Before'.}}
  @org.junit.After public void after() { } // Noncompliant {{Annotate this method with JUnit5 '@org.junit.jupiter.api.AfterEach' instead of JUnit4 '@After'.}}
}

class JUnitMethodDeclarationCheck_JUnit4_5_mixed2 {
  @org.junit.Test void junit4() { }
  @org.junit.jupiter.api.Test void junit5() { }

  // use JUnit 4 annotations
  @org.junit.BeforeClass public void setUp() { } // Noncompliant {{Annotate this method with JUnit5 '@org.junit.jupiter.api.BeforeAll' instead of JUnit4 '@BeforeClass'.}}
  @org.junit.AfterClass public void tearDown() { } // Noncompliant {{Annotate this method with JUnit5 '@org.junit.jupiter.api.AfterAll' instead of JUnit4 '@AfterClass'.}}
}
