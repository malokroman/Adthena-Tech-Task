import adthena.task.shoppingcart.ShoppingCart
import adthena.task.currencies.penceToString

import adthena.task.test.data.TestCatalogue

class ShoppingCartSuite extends munit.FunSuite:
  test("ShoppingCart should work perfectly when empty") {
    val shoppingCart = ShoppingCart(TestCatalogue)
    assertEquals(shoppingCart.calculateSubtotalPence, 0)
    assert(shoppingCart.listAppliedDiscounts().isEmpty)
    assertEquals(shoppingCart.processDiscounts(), 0)
  }

  test("ShoppingCart should add items when item is in the catalogue") {
    val shoppingCart = ShoppingCart(TestCatalogue)
    shoppingCart.addItemFromString("Apple")
    shoppingCart.addItemFromString("Milk")
    shoppingCart.addItemFromString("Soup")
    shoppingCart.addItemFromString("Bread")
  }

  test(
    "ShoppingCart should raise RuntimeException when item string not in the catalogue",
  ) {
    val shoppingCart = ShoppingCart(TestCatalogue)
    intercept[RuntimeException] {
      shoppingCart.addItemFromString("Missing Item")
    }
  }

  test("ShoppingCart should calculate subtotals according to cart content") {
    val shoppingCart = ShoppingCart(TestCatalogue)
    shoppingCart.addItemFromString("Milk")
    assertEquals(shoppingCart.calculateSubtotalPence, 130)
    shoppingCart.addItemFromString("Bread")
    assertEquals(shoppingCart.calculateSubtotalPence, 210)
  }

  test("ShoppingCart should calculate subtotals without discounts") {
    val shoppingCart = ShoppingCart(TestCatalogue)
    shoppingCart.addItemFromString("Apple")
    assertEquals(shoppingCart.calculateSubtotalPence, 100)
    shoppingCart.addItemFromString("Apple")
    assertEquals(shoppingCart.calculateSubtotalPence, 200)
  }

  test(
    "ShoppingCart should list all applied discounts that are eligible and only once",
  ) {
    val shoppingCart = ShoppingCart(TestCatalogue)
    shoppingCart.addItemFromString("Apple")
    assertEquals(shoppingCart.listAppliedDiscounts().size, 1)
    shoppingCart.addItemFromString("Apple")
    assertEquals(shoppingCart.listAppliedDiscounts().size, 1)
    shoppingCart.addItemFromString("Soup")
    shoppingCart.addItemFromString("Soup")
    shoppingCart.addItemFromString("Bread")
    assertEquals(shoppingCart.listAppliedDiscounts().size, 2)
  }

  test(
    "ShoppingCart processDiscounts() should print a message when no discounts available and return 0",
  ) {
    val shoppingCart = ShoppingCart(TestCatalogue)

    val stream = new java.io.ByteArrayOutputStream()
    var discountTotal: Int = 0
    Console.withOut(stream) {
      discountTotal = shoppingCart.processDiscounts()
    }
    assertEquals(discountTotal, 0)
    assert(stream.toString.contains("(No offers available)"))
  }

  test(
    "ShoppingCart processDiscounts() should print all individual discounts with correct discounted values and return the total",
  ) {
    val shoppingCart = ShoppingCart(TestCatalogue)

    shoppingCart.addItemFromString("Soup")
    shoppingCart.addItemFromString("Soup")
    for _ <- 1 to 10 do shoppingCart.addItemFromString("Apple")
    shoppingCart.addItemFromString("Bread")

    val stream = new java.io.ByteArrayOutputStream()
    var discountTotal: Int = 0
    Console.withOut(stream) {
      discountTotal = shoppingCart.processDiscounts()
    }
    assertEquals(discountTotal, 140)
    val out = stream.toString
    assert(out.contains(s"Apple 10% off: ${penceToString(100)}"))
    assert(
      out.contains(
        s"Loaf of Bread half price per two tins of soup: ${penceToString(40)}",
      ),
    )
  }

  test(
    "ShoppingCart processDiscounts() should NOT print inapplicable discounts",
  ) {
    val shoppingCart = ShoppingCart(TestCatalogue)
    shoppingCart.addItemFromString("Apple")

    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      shoppingCart.processDiscounts()
    }
    assert(
      !stream.toString.contains(
        "Loaf of Bread half price per two tins of soup:",
      ),
    )
  }

  test(
    "ShoppingCart checkout() should print the subtotal, discounts and total",
  ) {
    val shoppingCart = ShoppingCart(TestCatalogue)

    shoppingCart.addItemFromString("Soup")
    shoppingCart.addItemFromString("Soup")
    for _ <- 1 to 10 do shoppingCart.addItemFromString("Apple")
    shoppingCart.addItemFromString("Bread")
    shoppingCart.addItemFromString("Milk")

    val stream = new java.io.ByteArrayOutputStream()
    var total = 0
    Console.withOut(stream) {
      total = shoppingCart.checkout()
    }
    val out = stream.toString
    assertEquals(total, 1200)

    assert(out.contains(s"Subtotal: ${penceToString(1340)}"))
    assert(out.contains(s"Apple 10% off: ${penceToString(100)}"))
    assert(
      out.contains(
        s"Loaf of Bread half price per two tins of soup: ${penceToString(40)}",
      ),
    )
    assert(out.contains(s"Total price: ${penceToString(1200)}"))
  }
