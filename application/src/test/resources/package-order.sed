# Dependencies sort before LEIA
s/:(com[.]google|java|org[.](google|apache|neo4j|slf4j|spring))/:[10]\1/g
s/:(org[.]leialearns)/:[20]\1/g

# Order of top-level packages
s/([.]leialearns[.])(utilities[.])/\1[10]\2/g
s/([.]leialearns[.])(bridge[.])/\1[11]\2/g
s/([.]leialearns[.])(api[.])/\1[20]\2/g
s/([.]leialearns[.])(logic[.])/\1[30][A]\2/g
s/([.]leialearns[.])(graph[.])/\1[30][B]\2/g
s/([.]leialearns[.])(command[.])/\1[40]\2/g
s/([.]leialearns[.])(executable[.])/\1[50]\2/g

# Exceptions to order of sub-packages
s/([.]leialearns[.][[][0-9]*[]]([[][0-9]*[A-Z]])?api[.])(common[.](Locus|NodeDataProxy)|interaction[.]InteractionContext|session[.](Root|Session))[.]/\1[50]\3./g
s/([.]leialearns[.][[][0-9]*[]]([[][0-9]*[A-Z]])?graph[.])(interaction[.](InteractionContext[A-Z][A-Za-z]*)|session[.]SessionDAO)[.]/\1[50]\3./g
s/([.]leialearns[.][[][0-9]*[]]([[][0-9]*[A-Z]])?logic[.])(session[.]SessionAugmenter)[.]/\1[50]\3./g

# Order of sub-packages
s/([.]leialearns[.][[][0-9]*[]]([[][0-9]*[A-Z]])?[a-z]*[.])(enumerations[.])/\1[10]\3/g
s/([.]leialearns[.][[][0-9]*[]]([[][0-9]*[A-Z]])?[a-z]*[.])(utilities[.])/\1[11]\3/g
s/([.]leialearns[.][[][0-9]*[]]([[][0-9]*[A-Z]])?[a-z]*[.])(common[.])/\1[12]\3/g
s/([.]leialearns[.][[][0-9]*[]]([[][0-9]*[A-Z]])?[a-z]*[.])(interaction[.])/\1[20]\3/g
s/([.]leialearns[.][[][0-9]*[]]([[][0-9]*[A-Z]])?[a-z]*[.])(structure[.])/\1[30]\3/g
s/([.]leialearns[.][[][0-9]*[]]([[][0-9]*[A-Z]])?[a-z]*[.])(session[.])/\1[42]\3/g
s/([.]leialearns[.][[][0-9]*[]]([[][0-9]*[A-Z]])?[a-z]*[.])(model[.])/\1[45]\3/g
