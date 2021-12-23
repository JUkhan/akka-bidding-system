# akka-bidding-system

bidding system demo

### http://localhost:8088/api/bid

post data

```js
{
  "id": "SGu1Jpq1IO",
  "site": {
	"id": "0006a522ce0f4bbbbaa6b3c38cafaa0f",
	"domain": "fake.tld"
  },
  "device": {
	"id": "440579f4b408831516ebd02f6e1c31b4",
	"geo": {
  		"country": "LT"
	}
  },
  "imp": [
	{
  	"id": "1",
  	"h": 250,
  	"wmin": 300
	}
  ]
}

```

`Validation Process:` BidRequest validation process is little bit challanging/lengthy. For the solution of validation process mostly i used `pattern matching` and also `filter`, `map`, `flatMap`, `foldLeft` etc. Also I have added code comments before every logic implementation.
