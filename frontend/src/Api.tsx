import type { TriviaQuestion } from "./types";

const BASE_URL = "http://localhost:8080/";

const decodeHtml = (html: string) => {
  const txt = document.createElement("textarea");
  txt.innerHTML = html;
  return txt.value;
};

const parseToQuestionAndAnswers = (data: any): TriviaQuestion => {
  const item = data;

  const possibleAnswers: string[] = item.possible_answers;

  return {
    id: item.id,
    question: decodeHtml(item.question),
    possibleAnswers: possibleAnswers.map((a) => {
      return a;
    }),
  } as TriviaQuestion;
};

export const fetchQuestion = async (): Promise<TriviaQuestion | undefined> => {
  const endpoint = "questions";
  const response = await fetch(BASE_URL + endpoint);
  const data = await response.json();

  if (response.status !== 200 || !data || data.results.length == 0) {
    return undefined;
  }
  return parseToQuestionAndAnswers(data.results[0]);
};

export async function evaluateAnswer(
  id: string,
  givenAnswer: string,
): Promise<[boolean, string, string]> {
  const endpoint = "checkAnswers";

  const response = await fetch(BASE_URL + endpoint, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      answers: [
        {
          id,
          answer: givenAnswer,
        },
      ],
    }),
  });

  const data = await response.json();

  if (!response.ok || !data || !data.results || data.results.length === 0) {
    return [false, "", ""];
  }

  const result = data.results[0];

  return [result.isCorrect, result.correctAnswer, givenAnswer];
}
