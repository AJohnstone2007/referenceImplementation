let
fun f (x,y) = (x div y,x mod y)
val top = 536870911
val bot = ~536870912
in
if
f (5,3) = (1,2) andalso
f (~5,3) = (~2,1) andalso
f (5,~3) = (~2,~1) andalso
f (~5,~3) = (1,~2) andalso
f (105,10) = (10,5) andalso
f (1000000,100) = (10000,0) andalso
f (bot,1) = (bot,0) andalso
((ignore(f (bot,~1)); false) handle Overflow => true) andalso
((ignore(f (bot,0)); false) handle Div => true) andalso
((ignore(f (0,0)); false) handle Div => true) andalso
f (bot,~2) = (268435456, 0)
then print"Div test succeeded\n"
else print"Error: error in division test\n"
end;
