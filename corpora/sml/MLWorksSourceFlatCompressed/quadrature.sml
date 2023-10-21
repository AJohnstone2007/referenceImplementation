signature QUADRATURE =
sig
val integrate : (real -> real) * real * real * real option -> real
val differentiate : (real -> real) -> real -> real
end
;
