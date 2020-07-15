const CLUB_NAMES = [
  "SciFi Lovers Club", 
  "Justice in America United",
  "Mental Health in Literature Club",
  "Universe Investigators Coalition", 
  "Developing Programmers Coalition", 
];

const BOOKS_LIST = [
  {
    title: "Dune",
    author: "Frank Herbert", 
    isbn: "9780736692403",
  },
  {
    title: "Are Prisons Obsolete?",
    author: "Angela Davis",
    isbn: "9781583225813"
  },
  {
    title: "One Flew Over the Cuckoo's Nest",
    author: "Ken Kesey",
    isbn: "9780451163967",
  },
  {
    title: "Sapiens: A Brief History of Humankind",
    author: "Yuval Noah Harari", 
    isbn: "9780062316097"
  },
  {
    title: "Effective Java, Second Edition",
    author: "Joshua Bloch",
    isbn: "9780134685991",
  },
];

const CLUB_DESCRIPTIONS = [
  "This club aims to bring SciFi lovers together in their exploration of \
  world-building and universes different from our own. All are welcome!",
  "This club aims to bring about discussion concerning the current state \
  of the American prison system. Points of discussion include abolition, \
  alternative forms of policing and envisioning a world where community \
  infrastructure is prioritized over conventional policing. All are welcome!",
  "This club seeks to explore different forms of storytelling, including the \
  questionable narrator. Additionally, this club focuses on the portrayal of \
  sanity in popular writings.",
  "This clubs aims to bring greater understanding of the universe and \
  humanity's place in it. Topics include anthropology, human geography, and \
  astrophysics. All are welcome!",
  "This club aims to assist current and aspiring programmers in their \
  understaning of various programming languages and concepts. Points of \
  discussion include Java, language evolution, and programming best-practices.",
];

const CONTENT_WARNINGS = [
  ["Fantasy and SciFi violence", "Strong language"],
  ["Discussion of police brutality", 
   "Discussion of violence and injustices against minority groups."],
  ["Discussion of anxiety, depression, and mental illness",
    "Insensitive depictions of mental illness.",
    "Depictions of violence and strong language."],
  ["Discussion of religion and political systems."],
  ["Existential dread regarding career choice"],
];

export const CLUBS = [
  {
    name: CLUB_NAMES[0],
    description: CLUB_DESCRIPTIONS[0],
    contentWarnings: CONTENT_WARNINGS[0],
    currentBook: BOOKS_LIST[0],
  },
  {
    name: CLUB_NAMES[1],
    description: CLUB_DESCRIPTIONS[1],
    contentWarnings: CONTENT_WARNINGS[1],
    currentBook: BOOKS_LIST[1],
  },
  {
    name: CLUB_NAMES[2],
    description: CLUB_DESCRIPTIONS[2],
    contentWarnings: CONTENT_WARNINGS[2],
    currentBook: BOOKS_LIST[2],
  },
  {
    name: CLUB_NAMES[3],
    description: CLUB_DESCRIPTIONS[3],
    contentWarnings: CONTENT_WARNINGS[3],
    currentBook: BOOKS_LIST[3],
  },
  {
    name: CLUB_NAMES[4],
    description: CLUB_DESCRIPTIONS[4],
    contentWarnings: CONTENT_WARNINGS[4],
    currentBook: BOOKS_LIST[4],
  },
];
