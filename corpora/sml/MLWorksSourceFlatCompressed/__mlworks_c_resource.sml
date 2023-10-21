require "__mlworks_c_interface";
require "mlworks_c_resource";
structure MLWorksCResource : MLWORKS_C_RESOURCE =
struct
type 'a ptr = 'a MLWorksCInterface.ptr
fun withResource (close, resource, f)=
let
val r = (f resource) handle exn => (ignore(close resource); raise exn)
in
ignore(close resource);
r
end
fun withNonNullResource (close, exn, resource, f) =
if resource = MLWorksCInterface.null then
raise exn
else
withResource (close, resource, f)
end
;
