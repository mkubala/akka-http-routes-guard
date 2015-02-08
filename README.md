### akka-http-routes-guard

I found that very often Spray.io / Akka-http newcomers make common mistake - they forgot
to concatenate routes with the tilde (`~`) operator.

During [ScalaWAW hackathon](http://www.meetup.com/ScalaWAW/events/220284839/) I would like
to write a scala macro that aborts compilation (or at least raises a compiler warning)
when it encounters missing concatenation operator between routes.

ScalaWAW hackathon is a kind of the _Bring Your Own Laptop_ event and we have no guarantee
that network connection at the venue will be reliable.
Therefore it seems to be a good idea to clone this repository and fetch all the
dependencies in advance.
