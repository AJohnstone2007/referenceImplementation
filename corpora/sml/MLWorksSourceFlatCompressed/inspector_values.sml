signature INSPECTOR_VALUES =
sig
type Type
type options
val add_inspect_method : MLWorks.Internal.Value.T * Type -> unit
val delete_inspect_method : MLWorks.Internal.Value.T * Type -> unit
val delete_all_inspect_methods : unit -> unit
exception DuffUserMethod of exn
val get_inspector_values : options -> bool ->
(MLWorks.Internal.Value.T * Type) ->
(string * (MLWorks.Internal.Value.T * Type)) list
val type_eq : Type * Type -> bool
val is_scalar_value : (MLWorks.Internal.Value.T * Type) -> bool
val is_string_type : Type -> bool
val is_ref_type : Type -> bool
val string_abbreviation : string ref
end
;
