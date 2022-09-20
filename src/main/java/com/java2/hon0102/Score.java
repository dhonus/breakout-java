package com.java2.hon0102;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;


@NoArgsConstructor
@ToString
@Entity
@Table(name= "score")
public class Score implements Serializable {
	@Id
	@Column(name="id", nullable = false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Column(name = "score")
	private int score;

	public Score(int score) {
		this.score = score;
	}

	public int getScore() {
		return this.score;
	}

	public void add(int amount) {
		this.score += amount;
	}
}
