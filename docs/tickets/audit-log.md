# Ticket: Add an audit log

**Used by:** Exercise 2 (without instructions) and Exercise 3 (with instructions)

## As filed

> **Title:** Add an audit log
>
> We need to know who changed what. Add an audit log.

## That's it

This ticket is deliberately left exactly as a product owner would drop it into
a backlog at 5pm on a Friday. Do not refine it, do not clarify it, do not add
acceptance criteria. Exercises 2 and 3 depend on it staying vague — the whole
point is to watch what different models and different repo instructions do with
the same insufficient input.

If you want to know what a refined version of this would look like, that's
Exercise 4, on a different ticket.

## For reference while reviewing

Things this ticket does not say, that an implementation has to decide anyway:

- Which operations are auditable — create, update, delete, reads, logins?
- What's in an entry — who, what, when, before/after values?
- Where does it live — stdout, memory, a file? What happens on restart?
- Is it readable through the API? By whom? Only your own entries?
- Does an audit entry failing block the operation it was auditing?
