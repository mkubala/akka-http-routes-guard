## Quick introduction to the Scala's AST:

Scala code                          AST node

left.right              -->         Select(left, right)

sthg(args)              -->         Apply(stgh, args)

{
  expr1
  expr2
  expr3
}                       -->         Block(List(expr1, expr2), expr3) // list of statements + expression, that returns block's value

typeConstr[typeParams]  -->         TypeApply(typeConstr, typeParams)


## TODO

CODE:

macros:
- traverse routes tree, look for the missing concatenation tilde (`~`) operator

tests:
- write ScalaTest test cases (use macwire as a reference)

OTH:
If we'd like to make this project a part of the akka-http project:
- Create issue on Akka's GH
- Prepare PR to the Akka project


## Macro based project examples:

Macwire:
https://github.com/adamw/macwire