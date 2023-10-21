local
val top = 536870911;
val bot = ~536870912;
in
val t1 = (ignore(floor (~536870912.5)); false) handle Floor => true
val t2 = (ignore(floor (536870912.0)); false) handle Floor => true
val t3 = (floor (~536870912.0) = bot) handle Floor => false
val t4 = (floor (~536870911.5) = bot) handle Floor => false
val t5 = (floor (536870911.5) = top) handle Floor => false
val t6 = (floor (536870911.0) = top) handle Floor => false
val t7 = (floor (536870910.999) = top-1) handle Floor => false
val t8 = floor (2.0) = 2
val t9 = floor (2.8) = 2
val t10 = floor (~1.8) = ~2
val t11 = floor (~2.3) = ~3
val t12 = floor (~2.5) = ~3
val t13 = floor (~2.8) = ~3
val t14 = floor (0.0) = 0
val t15 = floor (~2.0) = ~2
val _ =
if t1 andalso t2 andalso t3 andalso t4 andalso t5 andalso t6 andalso t7 andalso t8 andalso
t9 andalso t10 andalso t11 andalso t12 andalso t13 andalso t14 andalso t15
then print"Floor test OK\n"
else print"Error: error in floor\n"
end
;
