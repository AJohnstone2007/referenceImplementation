structure Statistics =
struct
val System_Timer = ref (Timer.start_timer ())
fun start_system_timer () = (System_Timer := Timer.start_timer ())
fun check_system_timer () = Timer.check_timer (!System_Timer)
fun check_system_gc_timer () = Timer.check_timer_gc (!System_Timer)
fun check_system_total_timer () =
Timer.add_time (check_system_timer (), check_system_gc_timer ())
val Total_Match_Attempts = ref 0
val Part_Match_Attempts = ref 0
val Total_Match_Success = ref 0
val Part_Match_Success = ref 0
fun inc_match_attempts () =
(inc Total_Match_Attempts ; inc Part_Match_Attempts )
fun inc_match_success () =
(inc Total_Match_Success ; inc Part_Match_Success )
val Total_Unify_Attempts = ref 0
val Part_Unify_Attempts = ref 0
val Total_Unify_Success = ref 0
val Part_Unify_Success = ref 0
fun inc_unify_attempts () =
(inc Total_Unify_Attempts ; inc Part_Unify_Attempts )
fun inc_unify_success () =
(inc Total_Unify_Success ; inc Part_Unify_Success )
val Total_Critical_Pairs = ref 0
val Part_Critical_Pairs = ref 0
fun inc_critical_pair_count () =
(inc Total_Critical_Pairs ; inc Part_Critical_Pairs )
fun reset_part_statistics () =
(Part_Match_Attempts := 0 ;
Part_Match_Success := 0 ;
Part_Unify_Attempts := 0 ;
Part_Unify_Success := 0 ;
Part_Critical_Pairs := 0
)
fun reset_total_statistics () =
(Part_Match_Attempts := 0 ;
Part_Match_Success := 0 ;
Part_Unify_Attempts := 0 ;
Part_Unify_Success := 0 ;
Part_Critical_Pairs := 0 ;
Total_Match_Attempts := 0 ;
Total_Match_Success := 0 ;
Total_Unify_Attempts := 0 ;
Total_Unify_Success := 0 ;
Total_Critical_Pairs := 0
)
fun display_system_timings () =
map (newline o display_in_field Left 50)
[
" Total Execution Time          : " ^ "unknown",
" Total Garbage Collecting Time : " ^ "unknown",
" Total Run Time                : " ^ "unknown"
]
fun display_total_statistics x =
let
val Statistics_Menu = Menu.build_menu "" []
fun display_total_stats _ =
(display_two_cols Left
("Total",
[" Total Attempted Matches       : "^ makestring (!Total_Match_Attempts),
" Total Successful Matches      : "^ makestring (!Total_Match_Success),
"",
" Total Attempted Unifications  : "^ makestring (!Total_Unify_Attempts),
" Total Successful Unifications : "^ makestring (!Total_Unify_Success),
"",
" Total Critical Pairs          : "^ makestring (!Total_Critical_Pairs)
] ,
"Last Completion ",
["Attempted Matches       : "^ makestring (!Part_Match_Attempts),
"Successful Matches      : "^ makestring (!Part_Match_Success),
"",
"Attempted Unifications  : "^ makestring (!Part_Unify_Attempts),
"Successful Unifications : "^ makestring (!Part_Unify_Success),
"",
"Critical Pairs          : "^ makestring (!Part_Critical_Pairs)
]
) ;
print_line ();
display_system_timings ()
)
in
Menu.display_menu_screen 1 Statistics_Menu display_total_stats " System Statistics " "Statistics" x
end
fun display_partial_statistics () =
map (newline o display_in_field Left 50)
[" Attempted Matches  : "^ makestring (!Part_Match_Attempts),
" Successful Matches : "^ makestring (!Part_Match_Success),
" Attempted Unifies  : "^ makestring (!Part_Unify_Attempts),
" Successful Unifies : "^ makestring (!Part_Unify_Success),
" Critical Pairs     : "^ makestring (!Part_Critical_Pairs)
]
val RewriteMax = ref 1000
val RewriteCounter = ref 0
fun showRewriteMax () = !RewriteMax
fun setRewriteMax n = (RewriteMax := n)
fun resetRewriteCounter () = (RewriteCounter := 0)
fun incRewriteCounter () = (inc RewriteCounter)
fun testRewriteCounter () = (!RewriteCounter = !RewriteMax)
end
;
