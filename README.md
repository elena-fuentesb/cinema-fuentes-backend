# Backend for Cinema Fuentes

## Testing

Post movie
```
curl --request POST \
  --url http://localhost:9000/admin/movies \
  --header 'Content-Type: application/json' \
  --data '{
	"title" : "The Enigma",
	"genre": "War"
}'
```

Get movies
```
curl --request GET \
  --url http://localhost:9000/movies
```

Get auditoria
```
curl --request GET \
  --url http://localhost:9000/admin/cinema
```

Get schedule
```
curl --request GET \
  --url http://localhost:9000/schedule/20-03-2022
```

Schedule a movie
```
curl --request POST \
  --url http://localhost:9000/admin/schedule/movies \
  --header 'Content-Type: application/json' \
  --data '{
	"date": "20-03-2022",
	"movie" : "uMSBjKhp",
	"auditoriumName": "The Enigma Room"
}'
```

Make a booking
```
curl --request POST \
  --url http://localhost:9000/booking \
  --header 'Content-Type: application/json' \
  --data '{
	"name": "Elena Fuentes",
	"scheduledMovieId": 5306898920152001961,
	"seats": [
		{
		"row": "A",
		"column": 1
		}
	]
}'
```

Get reservations for movie
```
curl --request GET \
  --url http://localhost:9000/booking/scheduledmovie/5306898920152001961
```