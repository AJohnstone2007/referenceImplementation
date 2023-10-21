require "$.basis.__word";
signature NDBM =
sig
eqtype DBM
eqtype datum
eqtype c_int
eqtype c_uint
eqtype c_char
eqtype 'a c_ptr
val DBM'size' : Word.word
val DBM'addr' : DBM -> DBM c_ptr
val datum'size' : Word.word
val datum'addr' : datum -> datum c_ptr
val DBM'dbm_dirf'addr : DBM -> c_int c_ptr
val DBM'dbm_pagf'addr : DBM -> c_int c_ptr
val DBM'dbm_dirf : DBM -> c_int
val DBM'dbm_pagf : DBM -> c_int
val datum'dptr'addr : datum -> c_char c_ptr c_ptr
val datum'dsize'addr : datum -> c_uint c_ptr
val datum'dptr : datum -> c_char c_ptr
val datum'dsize : datum -> c_uint
val DBM_INSERT : c_int
val DBM_REPLACE : c_int
val dbm_open : c_char c_ptr * c_int * c_int -> DBM c_ptr
val dbm_store : DBM c_ptr * datum * datum * c_int -> c_int
val dbm_delete : DBM c_ptr * datum -> c_int
val dbm_fetch : DBM c_ptr * datum -> datum
val dbm_firstkey : DBM c_ptr -> datum
val dbm_nextkey : DBM c_ptr -> datum
val dbm_error : DBM c_ptr -> c_int
val dbm_clearerr : DBM c_ptr -> c_int
val dbm_close : DBM c_ptr -> unit
end
;
