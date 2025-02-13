import adthena.task.currencies.penceToString

class CurrencySuite extends munit.FunSuite:
  test("penceToString should append p if positive pence is smaller than 100") {
    assertEquals(penceToString(50), "50p")
    assertEquals(penceToString(99), "99p")
    assertEquals(penceToString(1), "1p")
  }

  test(
    "penceToString should print both the pound and remaining pence if pence is bigger than 100",
  ) {
    assertEquals(penceToString(150), "£1.50")
    assertEquals(penceToString(5719), "£57.19")
  }

  test(
    "penceToString should print correctly if remaining pence is smaller than 10",
  ) {
    assertEquals(penceToString(100), "£1.00")
    assertEquals(penceToString(105), "£1.05")
    assertEquals(penceToString(5700), "£57.00")
  }

  test(
    "penceToString should print 0 as 0p",
  ) {
    assertEquals(penceToString(0), "0p")
  }
  /* Behaviours of negative values are not defined */
