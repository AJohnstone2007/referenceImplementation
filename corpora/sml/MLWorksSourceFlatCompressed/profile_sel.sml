local
val lr : int list ref = ref []
fun mklist (0,l) = lr := l
| mklist (n,l) = mklist(n-1,n::l)
val manner = MLWorks.Profile.make_manner {time = false,
space = true,
calls = false,
copies = false,
depth = 0,
breakdown = []};
fun selector _ = (mklist(10,[]);
manner)
val options = MLWorks.Profile.Options {scan = 0, selector = selector}
val (result, profile) = MLWorks.Profile.profile options (fn () => ()) ()
in
val result = "Success"
end
;
