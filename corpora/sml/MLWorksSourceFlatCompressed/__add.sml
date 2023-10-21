require "add";
require "$.foreign.__mlworks_dynamic_library";
require "$.foreign.__mlworks_c_interface";
require "__add_stub";
structure Add : ADD =
struct
type c_int = MLWorksCInterface.Int.int
val add : c_int * c_int -> c_int = MLWorksDynamicLibrary.bind "add"
end
;
