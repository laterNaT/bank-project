package main

import (
	"context"
	"log"
	"net/http"
	"strings"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/segmentio/kafka-go"
)

type person struct {
	Key  string `json:"key"`
	Name string `json:"name"`
}

var persons = []person{
	{Key: "1", Name: "Jakob Pogulis"},
	{Key: "2", Name: "Xena"},
	{Key: "3", Name: "Marcus Bendtsen"},
	{Key: "4", Name: "Zorro"},
	{Key: "5", Name: "Q"},
}

func main() {
	router := gin.Default()
	router.GET("/person/list", getPersons)
	router.GET("/person/find.name/:name", getPersonByName)
	router.GET("/person/find.key/:key", getPersonByKey)

	router.Run("localhost:8090")
}

func getPersons(c *gin.Context) {
	writer("returning all persons")
	c.IndentedJSON(http.StatusOK, persons)
}

func getPersonByName(c *gin.Context) {
	name := c.Param("name")

	for _, person := range persons {
		if person.Name == name {
			writer("returning person by name: " + name)
			c.IndentedJSON(http.StatusOK, person)
			return
		}
	}
	writer("failed to find person by name: " + name)
	c.IndentedJSON(http.StatusNotFound, gin.H{"message": "name not found"})
}

func getPersonByKey(c *gin.Context) {
	key := c.Param("key")

	for _, person := range persons {
		if person.Key == key {
			writer("returning person by key: " + key)
			c.IndentedJSON(http.StatusOK, person)
			return
		}
	}
	writer("failed to find person by key: " + key)
	c.IndentedJSON(http.StatusNotFound, gin.H{"message": "key not found"})
}

func writer(msg string) {
	currentTime := strings.Split(time.Now().String(), ".")[0]

	w := &kafka.Writer{
		Addr:     kafka.TCP("localhost:9092"),
		Topic:    "restapi-topic",
		Balancer: &kafka.RoundRobin{},
	}
	err := w.WriteMessages(context.Background(),
		kafka.Message{
			Value: []byte(currentTime + " " + msg),
		},
	)
	if err != nil {
		log.Fatal("failed to write messages:", err)
	}

	if err := w.Close(); err != nil {
		log.Fatal("failed to close writer:", err)
	}
}
