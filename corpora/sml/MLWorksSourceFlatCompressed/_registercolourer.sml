require "../basis/__int";
require "../utils/lists";
require "../utils/crash";
require "../utils/diagnostic";
require "../utils/mutableintset";
require "../main/machspec";
require "virtualregister";
require "registercolourer";
functor RegisterColourer (structure Register : VIRTUALREGISTER where type T = int
structure MachSpec : MACHSPEC
structure Lists : LISTS
structure Crash : CRASH
structure Diagnostic : DIAGNOSTIC
structure IntSet : MUTABLEINTSET
val instance_name : Diagnostic.Text.T
val preassigned : Register.Pack.T
val available : Register.Pack.T
val debugging_available : Register.Pack.T
val temporaries : Register.T list
val corrupted_by_callee : Register.Pack.T
val reserved_but_preferencable : Register.Pack.T
val debugger_reserved_but_preferencable : Register.Pack.T
val allocation_equal : Register.T * Register.T -> bool
val allocation_order : Register.T * Register.T -> bool
sharing Diagnostic.Text = Register.Set.Text = IntSet.Text) : REGISTERCOLOURER =
struct
structure Register = Register
structure Diagnostic = Diagnostic
datatype ('a, 'b) union = INL of 'a | INR of 'b
val callee_save_temporaries =
Lists.filterp
(fn reg => not(Register.Pack.member(corrupted_by_callee, reg)))
temporaries
val nr_temporaries = Lists.length temporaries
val nr_callee_save_temporaries = Lists.length callee_save_temporaries
val callee_save_temporary_array =
MLWorks.Internal.Array.arrayoflist callee_save_temporaries
val have_spills = nr_temporaries <> 0
val have_callee_save_spills = nr_callee_save_temporaries <> 0
val do_preferencing = true
val do_diagnostics = false
val N = Int.toString
val $ = Diagnostic.Text.from_string
val ^^ = Diagnostic.Text.concatenate
infix ^^
fun diagnostic (level, output_function) =
if do_diagnostics
then
Diagnostic.output_text level
(fn verbosity => $"RegisterColourer (" ^^ instance_name ^^ $"): " ^^ output_function verbosity)
else
()
fun crash message = Crash.impossible ("RegisterColourer: " ^ message)
fun intsetforall f a =
IntSet.reduce
(fn (r,n) => r andalso f n)
(true,a)
val small_size = 32
fun order_vertices (graph, uses) =
let
val length = MLWorks.Internal.Array.length graph
in
if length <= small_size*2 then
let
fun order ({vertex=_, uses=uses : int}, {vertex=_, uses=uses'}) = uses >= uses'
fun list (l, 0) = l
| list (l, n) =
let
val n' = n-1
in
list ({vertex=n', uses=MLWorks.Internal.Array.sub (uses, n')}::l, n')
end
in
INL(Lists.qsort order (list ([], length)))
end
else
let
val indirections =
MLWorks.Internal.Array.tabulate (length, fn x => {vertex=x, uses=MLWorks.Internal.Array.sub(uses, x)})
fun find_smallest(result, 0) = result
| find_smallest(result as (best, {vertex=_, uses=value}), n) =
let
val n' = n-1
val curr as {uses=current, ...} = MLWorks.Internal.Array.sub(indirections, n')
in
find_smallest(if value > current then (n', curr) else result, n')
end
val initial_smallest =
find_smallest((0, (MLWorks.Internal.Array.sub(indirections, 0))), small_size)
fun try_one_element(n, smallest as (index, value), current) =
if #uses current > #uses value then
let
val _ = MLWorks.Internal.Array.update(indirections, index, current)
val _ = MLWorks.Internal.Array.update(indirections, n, value)
in
find_smallest((0, MLWorks.Internal.Array.sub(indirections, 0)), small_size)
end
else
smallest
val _ = MLWorks.Internal.ExtendedArray.reducel_index
try_one_element
(initial_smallest, indirections)
fun shuffle(from, to) =
if from >= to then ()
else
let
val to' = to-1
in
(MLWorks.Internal.Array.update(indirections, to, MLWorks.Internal.Array.sub(indirections, to'));
shuffle(from, to'))
end
fun find(0, _:int) = 0
| find(m, value) =
let
val m' = m-1
val {uses=curr, ...} = MLWorks.Internal.Array.sub(indirections, m')
in
if curr >= value then m else find(m', value)
end
fun insert(n, arg as {vertex=_, uses=value}) =
let
val n' = n-1
val {uses=prev, ...} = MLWorks.Internal.Array.sub(indirections, n')
in
if prev >= value then ()
else
let
val from = find(n', value)
val _ = shuffle(from, n)
in
MLWorks.Internal.Array.update(indirections, from, arg)
end
end
fun insert_all n =
if n >= small_size then ()
else
(insert(n, MLWorks.Internal.Array.sub(indirections, n));
insert_all(n+1))
in
INR(insert_all 1;
indirections)
end
end
fun make_colour_info(available, reserved_but_preferencable) =
let
val nr_colours = Register.Pack.cardinality available
val colours = MLWorks.Internal.Array.arrayoflist (Register.Pack.to_list available)
val colour_order =
let
fun sort list =
let
fun insert (list, register) =
let
fun insert' [] = [[register]]
| insert' ((e as r::rs) :: es) =
(if allocation_equal (register, r)
then
(register::r::rs) :: es
else
if allocation_order (register, r)
then
[register] :: e :: es
else
e :: insert' es)
| insert' _ = crash "insert"
in
insert' list
end
in
Lists.reducel insert ([], list)
end
fun convert list =
let
val length = Lists.length list
val new = MLWorks.Internal.Array.array (length, MLWorks.Internal.Array.array (0, 0))
fun colour register =
let
fun find 0 = crash "find"
| find n = if MLWorks.Internal.Array.sub (colours, n-1) = register then n-1 else find (n-1)
in
find nr_colours
end
fun fill (0, []) = new
| fill (n, e::es) =
(if do_diagnostics
then diagnostic (1, fn _ => $"Colour preference " ^^ $(N (n-1)) ^^
$" is " ^^ Register.Pack.to_text (Register.Pack.from_list e))
else ();
MLWorks.Internal.Array.update (new, n-1, MLWorks.Internal.Array.arrayoflist (map colour e));
fill (n-1, es))
| fill _ = crash "fill"
in
fill (length, list)
end
in
convert (sort (Register.Pack.to_list available))
end
in
(nr_colours, colours, colour_order, Register.Pack.cardinality available, reserved_but_preferencable)
end
val non_debug_colour_info = make_colour_info(available, reserved_but_preferencable)
val debug_colour_info = make_colour_info(debugging_available, debugger_reserved_but_preferencable)
datatype Colour = COLOUR of int | SLOT of int | RESERVED of Register.T | MERGE of Register.T | UNCOLOURED
type Graph = {nr_vertices : int,
graph : IntSet.T MLWorks.Internal.Array.array,
uses : int MLWorks.Internal.Array.array,
colouring : Colour MLWorks.Internal.Array.array}
local
val (nr_colours,colours,colour_order, _, _) = debug_colour_info
val map =
let
fun list (done, 0) = done
| list (done, n) = list ((MLWorks.Internal.Array.sub (colours, n-1), n-1)::done, n-1)
in
Register.Map.tryApply (Register.Map.from_list (list ([], nr_colours)))
end
in
fun debugging_preassigned_colour reg =
case map reg of
SOME x => COLOUR x
| _ => RESERVED reg
end
local
val (nr_colours,colours,colour_order, _, _) = non_debug_colour_info
val map =
let
fun list (done, 0) = done
| list (done, n) = list ((MLWorks.Internal.Array.sub (colours, n-1), n-1)::done, n-1)
in
Register.Map.tryApply (Register.Map.from_list (list ([], nr_colours)))
end
in
fun non_debugging_preassigned_colour reg =
case map reg of
SOME x => COLOUR x
| _ => RESERVED reg
end
fun empty(nr_registers, make_debugging_code) =
let
val preassigned_colour =
if make_debugging_code then debugging_preassigned_colour
else non_debugging_preassigned_colour
val colouring =
let
val colouring = MLWorks.Internal.Array.array (nr_registers, UNCOLOURED)
in
Register.Pack.iterate
(fn reg => MLWorks.Internal.Array.update (colouring, reg, preassigned_colour reg))
preassigned;
colouring
end
in
{nr_vertices = nr_registers,
graph = MLWorks.Internal.Array.array (nr_registers, IntSet.empty),
uses = MLWorks.Internal.Array.array (nr_registers, 0),
colouring = colouring}
end
fun both_coloured(colouring, reg, reg') =
case (MLWorks.Internal.Array.sub(colouring, reg),
MLWorks.Internal.Array.sub(colouring, reg')) of
(COLOUR _, COLOUR _) => true
| _ => false
fun clash({graph, uses, colouring, ...} : Graph, defined, referenced, live) =
(Register.Pack.iterate
(fn reg => MLWorks.Internal.Array.update (uses, reg, MLWorks.Internal.Array.sub (uses, reg) + 1))
referenced;
let
val card = Register.Pack.cardinality defined
in
if card > 0 then
Register.Pack.iterate
(fn reg =>
(MLWorks.Internal.Array.update (uses, reg, MLWorks.Internal.Array.sub (uses, reg) + 1);
Register.Pack.iterate
(fn reg' =>
if reg = reg' orelse both_coloured(colouring, reg, reg')
then ()
else
(MLWorks.Internal.Array.update (graph,reg',IntSet.add' (MLWorks.Internal.Array.sub (graph, reg'),reg));
MLWorks.Internal.Array.update (graph,reg,IntSet.add' (MLWorks.Internal.Array.sub (graph, reg),reg'))))
live;
(if card > 1 then
Register.Pack.iterate
(fn reg' =>
if reg = reg' orelse both_coloured(colouring, reg, reg')
then ()
else
(MLWorks.Internal.Array.update (graph,reg',IntSet.add' (MLWorks.Internal.Array.sub (graph, reg'),reg));
MLWorks.Internal.Array.update (graph,reg,IntSet.add' (MLWorks.Internal.Array.sub (graph, reg),reg'))))
defined
else
()))
)
defined
else
()
end
)
datatype assignment = REGISTER of Register.T | SPILL of int
fun copy_graph graph =
let
val graph' =
MLWorks.Internal.Array.tabulate
(MLWorks.Internal.Array.length graph,
fn i => IntSet.from_list(IntSet.to_list(MLWorks.Internal.Array.sub(graph, i))))
in
graph'
end
fun copy_colouring colouring = MLWorks.Internal.ExtendedArray.duplicate colouring
fun colour({nr_vertices, graph, uses, colouring},preferences,make_debugging_code, name) =
let
val (nr_colours,colours,colour_order, _, reserved_but_preferencable) =
if make_debugging_code then
debug_colour_info
else
non_debug_colour_info
val available = MLWorks.Internal.Array.array (nr_colours, true)
val available_spill = ref (MLWorks.Internal.Array.array (0, true))
val nr_spills = ref 0
val scatter = ref 0
fun real_colour r =
case MLWorks.Internal.Array.sub (colouring,r) of
MERGE r' => real_colour r'
| c => c
fun resolve r =
case MLWorks.Internal.Array.sub (colouring,r) of
MERGE r' => resolve r'
| _ => r
fun colour_preference (r,c) =
let
val ok =
intsetforall
(fn r' =>
case MLWorks.Internal.Array.sub (colouring,r') of
COLOUR c' => c <> c'
| MERGE _ => crash "MERGE in colour_preference"
| _ => true)
(MLWorks.Internal.Array.sub (graph,r))
in
if ok then
(
MLWorks.Internal.Array.update (colouring,r,COLOUR c))
else ()
end
fun merge (r1,r2) =
if r1 = r2 orelse IntSet.member (MLWorks.Internal.Array.sub (graph,r1),r2)
then ()
else
(MLWorks.Internal.Array.update (graph,r1,IntSet.union' (MLWorks.Internal.Array.sub (graph,r1),MLWorks.Internal.Array.sub (graph,r2)));
IntSet.iterate
(fn r =>
let
val set = MLWorks.Internal.Array.sub (graph,r)
in
MLWorks.Internal.Array.update (graph,r,IntSet.add'(IntSet.remove' (set,r2),r1))
end)
(MLWorks.Internal.Array.sub (graph,r2));
MLWorks.Internal.Array.update (colouring,r2,MERGE r1))
fun preference (r1,r2) =
let
val r1 = resolve r1
val r2 = resolve r2
in
case (MLWorks.Internal.Array.sub (colouring,r1),MLWorks.Internal.Array.sub (colouring,r2)) of
(COLOUR c,UNCOLOURED) => colour_preference (r2,c)
| (UNCOLOURED,COLOUR c) => colour_preference (r1,c)
| (UNCOLOURED,UNCOLOURED) => merge (r1,r2)
| (RESERVED r1',UNCOLOURED) =>
if Register.Pack.member(reserved_but_preferencable, r1') then
merge (r1',r2)
else ()
| (UNCOLOURED,RESERVED r2') =>
if Register.Pack.member(reserved_but_preferencable, r2') then
merge (r2',r1)
else ()
| _ => ()
end
fun neighbour vertex =
case MLWorks.Internal.Array.sub (colouring, vertex)
of COLOUR c => MLWorks.Internal.Array.update (available, c, false)
| SLOT s => MLWorks.Internal.Array.update (!available_spill, s, false)
| MERGE r => crash (N vertex ^ " = " ^ "MERGE " ^ N r ^ " in neighbour")
| _ => ()
fun find_spill (spill,limit,available) =
if spill = limit
then
let
val new_nr_spills = if limit = 0 then 4 else limit + limit
in
available_spill := MLWorks.Internal.Array.array (new_nr_spills,false);
nr_spills := limit+1;
SLOT limit
end
else
if MLWorks.Internal.Array.sub (available, spill) then
(if spill >= (!nr_spills) then nr_spills := spill+1 else ();
SLOT spill)
else
find_spill (spill+1,limit,available)
fun find_colour 0 =
let
val available = !available_spill
val limit = MLWorks.Internal.Array.length available
in
find_spill (0,limit,available)
end
| find_colour n =
let
val set = MLWorks.Internal.Array.sub (colour_order, n-1)
val length = MLWorks.Internal.Array.length set
fun find 0 = find_colour (n-1)
| find n =
let
val colour = MLWorks.Internal.Array.sub (set, (!scatter + n-1) mod length)
in
if MLWorks.Internal.Array.sub (available, colour) then
COLOUR colour
else
find (n-1)
end
in
scatter := !scatter+1;
find length
end
fun colour_one {vertex, uses} =
case MLWorks.Internal.Array.sub (colouring, vertex)
of UNCOLOURED =>
(MLWorks.Internal.ExtendedArray.fill (available, true);
MLWorks.Internal.ExtendedArray.fill (!available_spill, true);
IntSet.iterate neighbour (MLWorks.Internal.Array.sub (graph, vertex));
MLWorks.Internal.Array.update (colouring, vertex, find_colour (MLWorks.Internal.Array.length colour_order)))
| _ => ()
fun colour_all (INL x) = Lists.iterate colour_one x
| colour_all (INR x) = MLWorks.Internal.ExtendedArray.iterate colour_one x
val _ = if do_preferencing then Lists.iterate preference preferences else ()
val use_order = order_vertices(graph, uses)
val _ = colour_all use_order
val do_unspill =
let
val nr_spills = !nr_spills
in
0 < nr_spills andalso
nr_spills <= nr_callee_save_temporaries
end
val assign =
let
val assignments = MLWorks.Internal.Array.array (nr_vertices, SPILL 0)
fun assign 0 = ()
| assign n =
(MLWorks.Internal.Array.update
(assignments, n-1,
case real_colour(n-1) of
UNCOLOURED => crash "Colour: Unassigned registers after colouring"
| MERGE _ => crash "MERGE in assign"
| RESERVED reg => REGISTER reg
| COLOUR colour => REGISTER (MLWorks.Internal.Array.sub (colours, colour))
| SLOT spill => if do_unspill then REGISTER(MLWorks.Internal.Array.sub(callee_save_temporary_array, spill)) else SPILL spill);
assign (n-1))
in
assign nr_vertices;
if do_diagnostics then
diagnostic (2, fn _ =>
let
fun p (text, 0) = text
| p (text, n) =
p (text ^^ $"\n" ^^ $(N (n-1)) ^^ $" -> " ^^
(case MLWorks.Internal.Array.sub (assignments, n-1)
of REGISTER reg => Register.to_text reg
| SPILL slot => $(N slot)), n-1)
in
p ($"assignments", nr_vertices)
end)
else ();
fn reg => MLWorks.Internal.Array.sub (assignments, reg)
end
in
{assign = assign,
nr_spills = if do_unspill then 0 else !nr_spills}
end
fun resolve colouring =
let
fun res r =
case MLWorks.Internal.Array.sub (colouring,r) of
MERGE r' => res r'
| _ => r
in
res
end
fun find_low_degree_unassigned_node(graph, colouring, nodes_used, real_colours_available) =
let
val resolve = resolve colouring
fun do_node (0, res) = res
| do_node (i, res) =
let
val i' = i-1
in
if resolve i' <> i' then
do_node(i', res)
else
case Register.Map.tryApply'(nodes_used, i') of
SOME _ => do_node(i', res)
| NONE =>
let
val set = MLWorks.Internal.ExtendedArray.sub(graph, i')
val degree' = IntSet.cardinality set
in
case res of
SOME(node, degree) =>
if degree' < degree then
let
val res = SOME(i', degree')
in
if degree' < real_colours_available then
SOME(i', degree')
else
do_node(i', res)
end
else
do_node(i', res)
| NONE =>
let
val res = SOME(i', degree')
in
if degree' <= real_colours_available then
res
else
do_node(i', res)
end
end
end
in
do_node(MLWorks.Internal.ExtendedArray.length graph, NONE)
end
fun assign_stack(nr_vertices, stack, info as (nr_colours, colours, colour_order, _, _),
colouring, graph) =
let
val available = MLWorks.Internal.Array.array(nr_colours, true)
val available_spill = ref (MLWorks.Internal.Array.array (0, true))
val nr_spills = ref 0
fun find_spill(spill, limit, available) =
if spill = limit
then
let
val new_nr_spills =
if limit = 0
then 4
else limit + limit
in
available_spill := MLWorks.Internal.Array.array(new_nr_spills, false);
nr_spills := limit+1;
SLOT limit
end
else
if MLWorks.Internal.Array.sub(available, spill) then
(if spill >= (!nr_spills) then nr_spills := spill+1 else ();
SLOT spill)
else
find_spill (spill+1,limit,available)
val scatter = ref 0
fun find_colour 0 =
let
val available = !available_spill
val limit = MLWorks.Internal.Array.length available
in
find_spill(0, limit, available)
end
| find_colour n =
let
val set = MLWorks.Internal.Array.sub(colour_order, n-1)
val length = MLWorks.Internal.Array.length set
fun find 0 = find_colour(n-1)
| find n =
let
val colour = MLWorks.Internal.Array.sub(set, (!scatter + n-1) mod length)
in
if MLWorks.Internal.Array.sub(available, colour) then
COLOUR colour
else
find(n-1)
end
in
scatter := !scatter+1;
find length
end
fun neighbour vertex =
case MLWorks.Internal.Array.sub(colouring, vertex) of
COLOUR c => MLWorks.Internal.Array.update(available, c, false)
| SLOT s => MLWorks.Internal.Array.update(!available_spill, s, false)
| MERGE r => crash(N vertex ^ " = " ^ "MERGE " ^ N r ^ " in neighbour")
| _ => ()
fun colour_one vertex =
case MLWorks.Internal.Array.sub(colouring, vertex) of
UNCOLOURED =>
(MLWorks.Internal.ExtendedArray.fill(available, true);
MLWorks.Internal.ExtendedArray.fill(!available_spill, true);
IntSet.iterate neighbour (MLWorks.Internal.Array.sub(graph, vertex));
MLWorks.Internal.Array.update(colouring, vertex, find_colour(MLWorks.Internal.Array.length colour_order)))
| _ => ()
val _ = Lists.iterate colour_one stack
fun real_colour r =
case MLWorks.Internal.Array.sub (colouring,r) of
MERGE r' => real_colour r'
| c => c
val do_unspill =
let
val nr_spills = !nr_spills
in
0 < nr_spills andalso
nr_spills <= nr_callee_save_temporaries
end
val assign =
let
val assignments = MLWorks.Internal.Array.array (nr_vertices, SPILL 0)
fun assign 0 = ()
| assign n =
(MLWorks.Internal.Array.update
(assignments, n-1,
case real_colour(n-1) of
UNCOLOURED => crash "Stack Colour: Unassigned registers after colouring"
| MERGE _ => crash "MERGE in assign"
| RESERVED reg => REGISTER reg
| COLOUR colour => REGISTER (MLWorks.Internal.Array.sub (colours, colour))
| SLOT spill => if do_unspill then REGISTER(MLWorks.Internal.Array.sub(callee_save_temporary_array, spill)) else SPILL spill);
assign (n-1))
in
assign nr_vertices;
fn reg => MLWorks.Internal.Array.sub (assignments, reg)
end
in
{assign = assign,
nr_spills = if do_unspill then 0 else !nr_spills}
end
fun stack_colour'
(nr_vertices, info, graph, uses, colouring, preferences, do_preference, real_colours_available, name, reserved_but_preferencable) =
let
fun resolve r =
case MLWorks.Internal.Array.sub(colouring,r) of
MERGE r' => resolve r'
| _ => r
fun colour_preference(r, c) =
let
val ok =
intsetforall
(fn r' =>
case MLWorks.Internal.Array.sub(colouring, r') of
COLOUR c' => c <> c'
| MERGE _ => crash "MERGE in colour_preference"
| _ => true)
(MLWorks.Internal.Array.sub(graph, r))
in
if ok then
(MLWorks.Internal.Array.update(colouring, r, COLOUR c))
else ()
end
fun merge(r1, r2) =
if r1 = r2 orelse IntSet.member(MLWorks.Internal.Array.sub(graph, r1), r2)
then ()
else
(
MLWorks.Internal.Array.update(graph, r1, IntSet.union'(MLWorks.Internal.Array.sub(graph, r1), MLWorks.Internal.Array.sub(graph, r2)));
IntSet.iterate
(fn r =>
let
val set = MLWorks.Internal.Array.sub(graph, r)
in
MLWorks.Internal.Array.update(graph, r, IntSet.add'(IntSet.remove'(set, r2), r1))
end)
(MLWorks.Internal.Array.sub(graph, r2));
MLWorks.Internal.Array.update(colouring, r2, MERGE r1))
fun preference (r1, r2) =
let
val r1 = resolve r1
val r2 = resolve r2
in
case (MLWorks.Internal.Array.sub(colouring, r1), MLWorks.Internal.Array.sub(colouring, r2)) of
(COLOUR c, UNCOLOURED) => colour_preference(r2, c)
| (UNCOLOURED, COLOUR c) => colour_preference(r1, c)
| (UNCOLOURED, UNCOLOURED) => merge(r1, r2)
| (RESERVED r1',UNCOLOURED) =>
if Register.Pack.member(reserved_but_preferencable, r1') then
merge (r1',r2)
else ()
| (UNCOLOURED,RESERVED r2') =>
if Register.Pack.member(reserved_but_preferencable, r2') then
merge (r2',r1)
else ()
| _ => ()
end
val _ =
if do_preference then
Lists.iterate preference preferences
else
()
val unshrunk_graph = copy_graph graph
fun stack_colour(stack, nodes_used) =
case find_low_degree_unassigned_node(graph, colouring, nodes_used, real_colours_available) of
NONE => stack
| SOME(node, degree) =>
(MLWorks.Internal.Array.update(graph, node, IntSet.empty);
MLWorks.Internal.ExtendedArray.iterate_index
(fn (i, set) =>
MLWorks.Internal.Array.update(graph, i, IntSet.remove'(set, node)))
graph;
stack_colour(node :: stack, Register.Map.define(nodes_used, node, true)))
in
assign_stack(nr_vertices, stack_colour([], Register.Map.empty), info, colouring, unshrunk_graph)
end
val colour =
fn (args as ({nr_vertices, graph, uses, colouring},
preferences,make_debugging_code, name)) =>
if MachSpec.use_stack_colourer then
let
val info as (_, _, _, real_colours_available, reserved_but_preferencable) =
if make_debugging_code then debug_colour_info else non_debug_colour_info
fun ignore_stack_colourer s =
let
fun is_prefix n =
let
val l = size n
in
size s > l andalso MLWorks.String.substring (s,0,l) = n
end
in
is_prefix "<Setup>" orelse is_prefix "Functor "
end
val ignore = ignore_stack_colourer name
val (result as {nr_spills, ...}, result' as {nr_spills=nr_spills', ...}) =
if ignore then
let
val result = colour args
in
(result, result)
end
else
let
val graph' = copy_graph graph
val colouring' = copy_colouring colouring
val result as {nr_spills, ...} = colour args
val result' =
if nr_spills = 0 orelse nr_spills > 3 then
result
else
stack_colour'
(nr_vertices, info, graph', uses, colouring', preferences, do_preferencing, real_colours_available, name, reserved_but_preferencable)
in
(result, result')
end
in
if nr_spills' = 0 then result' else result
end
else
colour args
end
;
