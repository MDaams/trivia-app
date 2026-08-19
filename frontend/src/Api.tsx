import type { QuestionAndAnswer } from "./types";

const sleep = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));

const decodeHtml = (html: string) => {
  const txt = document.createElement("textarea");
  txt.innerHTML = html;
  return txt.value;
};

let questionAndAnswers: QuestionAndAnswer[] = [];

const makeBooleanHumanReadable = (value: string): string => {
  if (value.trim().toLowerCase() === "true") return "Yes";
  else if (value.trim().toLowerCase() === "false") return "No";
  return value;
};

const parseToQuestionAndAnswers = (data: any) => {
  questionAndAnswers = [];
  for (let i = 0; i < data.results.length; i++) {
    const item = data.results[i];

    // Translate answers
    const correctAnswer = makeBooleanHumanReadable(
      decodeHtml(item.correct_answer),
    );

    const otherAnswers = item.incorrect_answers.map((a: string) =>
      makeBooleanHumanReadable(a),
    );

    // Combine answers
    const allAnswers = [...otherAnswers, correctAnswer];

    // Shuffle answers
    const shuffledAnswers = allAnswers.sort(() => Math.random() - 0.5);

    questionAndAnswers.push({
      id: i,
      question: decodeHtml(item.question),
      answers: shuffledAnswers.map((ans: string) => decodeHtml(ans)),
      correctAnswer: correctAnswer,
      isAnswered: false,
    });
  }
};

const fetchBulkFromTriviaAPI = async () => {
  const response = await fetch("https://opentdb.com/api.php?amount=20");
  const data = await response.json();

  if (response.status !== 200 || !data || data.results.length == 0) {
    return undefined;
  }
  parseToQuestionAndAnswers(data);
};

const needsFreshQuestions = (): boolean => {
  const unAnsweredQuestions = questionAndAnswers.filter((qa) => !qa.isAnswered);
  return unAnsweredQuestions.length === 0;
};

export async function mockFetchQuestion(): Promise<QuestionAndAnswer> {
  await sleep(1000);

  if (needsFreshQuestions()) {
    console.log("Fetching bulk");
    await fetchBulkFromTriviaAPI();
  }

  console.log("Returning new question");
  console.log("QA's", questionAndAnswers);

  const freshQuestion = questionAndAnswers.filter((qa) => !qa.isAnswered)[0];
  return freshQuestion;
}

export async function mockValidateAnswer(
  id: number,
  givenAnswer: string,
): Promise<[boolean, string, string]> {
  await sleep(500);

  const foundQA = questionAndAnswers.find((qa) => qa.id == id);

  if (!foundQA) {
    return [false, "unknown", givenAnswer];
  }

  const foundIndex = questionAndAnswers.indexOf(foundQA);
  questionAndAnswers[foundIndex] = { ...foundQA, isAnswered: true };

  return [
    givenAnswer.trim() === foundQA.correctAnswer.trim(),
    foundQA.correctAnswer,
    givenAnswer,
  ];
}
