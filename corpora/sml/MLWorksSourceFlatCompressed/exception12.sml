exception E of exn;
exception A of exn;
exception B = A;
exception C of exn;
exception D = C;
exception F;
fun f (E(E(E(A(E(E(E(C(E(E(E(B(E(E(E(D(F))))))))))))))))) = 1
| f (E(E(E(B(E(E(E(D(E(E(E(A(E(E(E(C(F))))))))))))))))) = 2
| f (E(E(E(C(E(E(E(E(B(E(E(E(E(E(E(B(F))))))))))))))))) = 3
| f (E(E(E(D(E(E(E(E(A(E(E(E(E(E(E(C(F))))))))))))))))) = 4;
f (E(E(E(B(E(E(E(D(E(E(E(A(E(E(E(C(F)))))))))))))))));
f (E(E(E(B(E(E(E(D(E(E(E(A(E(E(E(C(F)))))))))))))))));
f (E(E(E(D(E(E(E(E(A(E(E(E(E(E(E(A(F)))))))))))))))));
f (E(E(E(C(E(E(E(E(B(E(E(E(E(E(E(D(F)))))))))))))))));
