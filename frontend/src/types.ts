export interface TriviaQuestion {
  id: string;
  possibleAnswers: string[];
  question: string;
}

export interface TriviaQuestionDTO {
  id: string;
  possible_answers: string[];
  question: string;
}

export interface TriviaQuestionResponseDTO {
  results: TriviaQuestionDTO[];
}
