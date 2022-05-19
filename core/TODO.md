 * DONE: Map mit einem int-key
 * DONE: Gibts in der JVM ne map mit primitivem typ int? evtl IdentityHashMap?
 * DONE: Initiale capacity auf der Map setzen.
 * DONE: Evtl ne @Default-annotation für default-texte.
 * Evtl Name-transformer auf dem Bundle als annotation? 
      So ne art @Rename? wo man alles global renamen kann?
 * TODO: Exclude things from Object (like equals)
 * ---> for reflection
 * ---> but also for proxy
* Noch was machen mit dynamischen properties... z.b. generieren von listen...
* Glaube das interface MsgSource müssten wir noch besser machen... glaube es braucht 2 interfaces,
  wohl noch eines wo die "default"-strategie kriegt... Throw oder default... Villeicht so was:
   -> MsgSource -> Hier kann ich nichts kontrollieren. Das wird dann aber dem dynamichen property gegeben.
   -> AdvancedMsgSource -> da kann ich kontrollieren was passiert (null, throw und so...)

https://codereview.stackexchange.com/questions/182713/a-fast-integer-key-map-in-java-via-a-van-emde-boas-tree
https://github.com/MDhondt/vEB

# DONE: Marker

Glaube es braucht nicht zwinend nen marker. Man könnte wohl auch so:
 - MsgRes ist ein Interface mit einer "construct" methode (erweitert "Res")
 - Heutige MsgRes klasse ist ne versteckte inner klasse von MsgRes.
 - MsgMarker könnte dann weg.