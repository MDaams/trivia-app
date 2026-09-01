import type {
  CheckAnswersDTO,
  CheckAnswersResponseDTO,
  EvaluateAnswer,
  TriviaQuestion,
  TriviaQuestionDTO,
  TriviaQuestionResponseDTO,
} from "./types";

const BASE_URL = "http://localhost:8080/";

const parseToQuestionAndAnswers = (data: TriviaQuestionDTO): TriviaQuestion => {
  const item = data;

  const possibleAnswers: string[] = item.possible_answers;

  return {
    id: item.id,
    question: item.question,
    possibleAnswers: possibleAnswers.map((a) => {
      return a;
    }),
  } as TriviaQuestion;
};

export const fetchQuestion = async (): Promise<TriviaQuestion> => {
  const endpoint = "questions?amount=1";
  try {
    const response = await fetch(BASE_URL + endpoint);
    const data: TriviaQuestionResponseDTO = await response.json();

    if (!response.ok || !data || !data.results || data.results.length === 0) {
      throw new Error(
        data.error || "A problem occurred while loading question.",
      );
    }

    return parseToQuestionAndAnswers(data.results[0]);
  } catch (error) {
    throw new Error(
      error instanceof Error ? error.message : "Unable to connect to server.",
    );
  }
};

export async function evaluateAnswer(
  id: string,
  givenAnswer: string,
): Promise<EvaluateAnswer> {
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

  const data: CheckAnswersResponseDTO = await response.json();

  if (!response.ok || !data || !data.results || data.results.length === 0) {
    return {
      id,
      error: data.error || "A problem occurred while checking answer.",
    };
  }

  const result: CheckAnswersDTO = data.results[0];

  return {
    id: result.id,
    isCorrect: result.isCorrect,
    correctAnswer: result.correctAnswer,
  };
}
