package adthena.task.currencies

import math.Integral.Implicits.infixIntegralOps

def penceToString(pence: Int): String =
  if pence < 100 && pence > -100 then s"${pence}p"
  else
    val (pound, remainder) = pence /% 100
    f"£${pound}.${remainder}%02d"
