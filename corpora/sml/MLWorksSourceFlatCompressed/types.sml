require "../typechecker/datatypes";
require "../main/options";
signature TYPES =
sig
structure Datatypes : DATATYPES
structure Options: OPTIONS
val make_tyname : (int * bool * string * string option
* int) -> Datatypes.Tyname
val eq_attrib : Datatypes.Tyname -> bool
val has_ref_equality : Datatypes.Tyname -> bool
val has_int_equality : Datatypes.Tyname -> bool
val has_real_equality : Datatypes.Tyname -> bool
val has_string_equality : Datatypes.Tyname -> bool
val has_int32_equality : Datatypes.Tyname -> bool
val print_name : Options.options -> Datatypes.Tyname -> string
val debug_print_name : Datatypes.Tyname -> string
val tyname_arity : Datatypes.Tyname -> int
val tyname_make_false : Datatypes.Tyname -> bool
val tyname_eq : Datatypes.Tyname * Datatypes.Tyname -> bool
val tyname_conenv : Datatypes.Tyname -> Datatypes.Valenv
val tyname_strip : Datatypes.Tyname -> Datatypes.Tyname
val bool_tyname : Datatypes.Tyname
val int_tyname : Datatypes.Tyname
val word_tyname : Datatypes.Tyname
val int8_tyname : Datatypes.Tyname
val word8_tyname : Datatypes.Tyname
val int16_tyname : Datatypes.Tyname
val word16_tyname : Datatypes.Tyname
val int32_tyname : Datatypes.Tyname
val word32_tyname : Datatypes.Tyname
val int64_tyname : Datatypes.Tyname
val word64_tyname : Datatypes.Tyname
val real_tyname : Datatypes.Tyname
val float32_tyname : Datatypes.Tyname
val string_tyname : Datatypes.Tyname
val char_tyname : Datatypes.Tyname
val list_tyname : Datatypes.Tyname
val ref_tyname : Datatypes.Tyname
val exn_tyname : Datatypes.Tyname
val ml_value_tyname : Datatypes.Tyname
val array_tyname : Datatypes.Tyname
val vector_tyname : Datatypes.Tyname
val bytearray_tyname : Datatypes.Tyname
val floatarray_tyname : Datatypes.Tyname
val dynamic_tyname : Datatypes.Tyname
val typerep_tyname : Datatypes.Tyname
val real_tyname_equality_attribute: bool ref
val cons_typep : Datatypes.Type -> bool
val imperativep : Datatypes.Type -> bool
val tyvar_equalityp : Datatypes.Type -> bool
val type_equalityp : Datatypes.Type -> bool
val closed_type_equalityp : Datatypes.Type -> bool
val type_eq : Datatypes.Type * Datatypes.Type * bool * bool -> bool
val simplify_type : Datatypes.Type -> Datatypes.Type
val int_typep : Datatypes.Type -> bool
val real_typep : Datatypes.Type -> bool
val word_typep : Datatypes.Type -> bool
val num_typep : Datatypes.Type -> bool
val num_or_string_typep : Datatypes.Type -> bool
val wordint_typep : Datatypes.Type -> bool
val realint_typep : Datatypes.Type -> bool
val print_type : Options.options -> Datatypes.Type -> string
val debug_print_type : Options.options ->
Datatypes.Type -> string
val extra_debug_print_type : Datatypes.Type -> string
val print_tyvars: Options.options -> Datatypes.Type list -> string
val type_of : Datatypes.Ident.SCon -> Datatypes.Type
val has_free_imptyvars : Datatypes.Type -> Datatypes.Type option
val type_occurs : Datatypes.Type * Datatypes.Type -> bool
val the_type : Datatypes.Type -> Datatypes.Type
val tyvars : Datatypes.Ident.TyVar list * Datatypes.Type -> Datatypes.Ident.TyVar list
val all_tyvars : Datatypes.Type -> (int * Datatypes.Type * Datatypes.Instance) ref list
val make_tyvars: int -> Datatypes.Type list
val int_type : Datatypes.Type
val word_type : Datatypes.Type
val int8_type : Datatypes.Type
val word8_type : Datatypes.Type
val int16_type : Datatypes.Type
val word16_type : Datatypes.Type
val int32_type : Datatypes.Type
val word32_type : Datatypes.Type
val int64_type : Datatypes.Type
val word64_type : Datatypes.Type
val real_type : Datatypes.Type
val float32_type : Datatypes.Type
val string_type : Datatypes.Type
val char_type : Datatypes.Type
val bool_type : Datatypes.Type
val exn_type : Datatypes.Type
val ml_value_type : Datatypes.Type
val dynamic_type : Datatypes.Type
val typerep_type : Datatypes.Type
val sizeof: Datatypes.Type -> int option
val resolve_overloading:
bool * Datatypes.Type *
(Datatypes.Ident.ValId * Datatypes.Ident.Location.T -> unit) ->
unit
val empty_rectype : Datatypes.Type
val rectype_domain : Datatypes.Type -> Datatypes.Ident.Lab list
val rectype_range : Datatypes.Type -> Datatypes.Type list
val add_to_rectype : (Datatypes.Ident.Lab * Datatypes.Type
* Datatypes.Type) -> Datatypes.Type
val get_type_from_lab : (Datatypes.Ident.Lab * Datatypes.Type)
-> Datatypes.Type
val isFunType : Datatypes.Type -> bool
exception ArgRes
val argres : Datatypes.Type -> Datatypes.Type * Datatypes.Type
val tyfun_strip : Datatypes.Tyfun -> Datatypes.Tyfun
val null_tyfunp : Datatypes.Tyfun -> bool
val make_tyfun : Datatypes.Ident.TyVar list * Datatypes.Type ->
Datatypes.Tyfun
val make_eta_tyfun : Datatypes.Tyname -> Datatypes.Tyfun
val apply : Datatypes.Tyfun * Datatypes.Type list -> Datatypes.Type
val has_a_name : Datatypes.Tyfun -> bool
val meta_tyname : Datatypes.Tyfun -> Datatypes.Tyname
val name : Datatypes.Tyfun -> Datatypes.Tyname
val arity : Datatypes.Tyfun -> int
val tyfun_eq : Datatypes.Tyfun * Datatypes.Tyfun -> bool
val equalityp : Datatypes.Tyfun -> bool
val make_false : Datatypes.Tyfun -> bool
val make_true : Datatypes.Tyname -> unit
val string_tyfun : Datatypes.Tyfun -> string
val update_tyfun_instantiations : Datatypes.Tyfun -> int
val fetch_tyfun_instantiation : int -> Datatypes.Tyfun
val string_debruijn : Options.options * int * bool * bool -> string
val check_debruijns : Datatypes.Type list * int -> bool
val create_tyname_copy :
bool -> int ->
Datatypes.Tyname Datatypes.StampMap * Datatypes.Tyname ->
Datatypes.Tyname Datatypes.StampMap
val tyname_copy : Datatypes.Tyname * Datatypes.Tyname Datatypes.StampMap
-> Datatypes.Tyname
val tyfun_copy : Datatypes.Tyfun * Datatypes.Tyname Datatypes.StampMap
-> Datatypes.Tyfun
val type_copy : Datatypes.Type * Datatypes.Tyname Datatypes.StampMap
-> Datatypes.Type
type seen_tyvars
val no_tyvars : seen_tyvars
val print_type_with_seen_tyvars :
Options.options * Datatypes.Type * seen_tyvars -> string * seen_tyvars
val type_has_unbound_tyvars : Datatypes.Type -> bool
val tyfun_has_unbound_tyvars : Datatypes.Tyfun -> bool
exception CombineTypes
val combine_types : Datatypes.Type * Datatypes.Type -> Datatypes.Type
val stamp_num : Datatypes.Stamp -> int
val make_stamp : unit -> Datatypes.Stamp
val pervasive_stamp_count : int
end;
